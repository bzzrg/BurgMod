package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import com.bzzrg.burgmod.utils.simulation.PlayerSim;
import com.bzzrg.burgmod.utils.simulation.UpdateSimOptions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.basicconfig.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig.*;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.InputType.AIR;
import static com.bzzrg.burgmod.utils.GeneralUtils.*;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.createSim;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.updateSim;

public class P45OffsetLabel {

    private static int tickNum = 0;
    private static boolean finished = false;
    public static BlockPos landingPos = null;
    private static boolean falseStrategy = false;
    private static final List<UpdateSimOptions> extendedTicks = new ArrayList<>();

    private static float jumpAngle = 0;

    public static String autoLabel = color1 + "Perfect 45 Offset (?): \u00A7r?";
    public static String xLabel = color1 + "Perfect 45 Offset (X?): \u00A7r?";
    public static String zLabel = color1 + "Perfect 45 Offset (Z?): \u00A7r?";

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (P45OffsetConfig.enabled) {
            if (showAutoOffset) {
                mc.fontRendererObj.drawStringWithShadow(autoLabel, P45OffsetConfig.autoLabelX, P45OffsetConfig.autoLabelY, 0xFFFFFF);
            }
            if (showXOffset) {
                mc.fontRendererObj.drawStringWithShadow(xLabel, P45OffsetConfig.xLabelX, P45OffsetConfig.xLabelY, 0xFFFFFF);
            }
            if (showZOffset) {
                mc.fontRendererObj.drawStringWithShadow(zLabel, P45OffsetConfig.zLabelX, P45OffsetConfig.zLabelY, 0xFFFFFF);
            }
        }
    }

    public static void onReset() {

        tickNum = 0;
        finished = false;
        landingPos = null;
        falseStrategy = false;
        extendedTicks.clear();

        jumpAngle = (((getJumpAngle() == null ? mc.thePlayer.rotationYaw : getJumpAngle()) + 180) % 360 + 360) % 360 - 180; // can be values -180 <= value < 180, 180 outputs -180 which is expected

        xLabel = String.format(color1 + "%s45 Offset (X%s): \u00A7r?", shortenLabels ? "P" : "Perfect ", jumpAngle <= 0 ? "+" : "-");
        zLabel = String.format(color1 + "%s45 Offset (Z%s): \u00A7r?", shortenLabels ? "P" : "Perfect ", (-90 <= jumpAngle && jumpAngle <= 90) ? "+" : "-");

        autoLabel = (EnumFacing.fromAngle(jumpAngle).getAxis() == EnumFacing.Axis.X) ? xLabel : zLabel;
        if (isConfigInvalid()) return;

        // Get needed variables
        List<Integer> jumpTickIndices = new ArrayList<>();
        for (int i = 0;; i++) {
            StrategyTick jumpTick = StrategyTick.getJumpTick(i);
            if (jumpTick == null) break;
            jumpTickIndices.add(jumpTick.getTickNum());
        }
        Collections.reverse(jumpTickIndices);
        List<Integer> jumpTick45Indices = jumpTickIndices.subList(jumpTickIndices.size() - numOf45s, jumpTickIndices.size());

        int lastJumpIndex = jumpTickIndices.get(jumpTickIndices.size()-1);
        Set<InputType> lastInputs = strategyTicks.get(strategyTicks.size()-1).correctInputs;

        // Get jump pos and landing pos
        PlayerSim sim1 = createSim();
        updateSim(sim1, null);

        BlockPos jumpPos = null; // Never null in practice after the for loop below this
        for (int i = 0; i < lastJumpIndex + 100; i++) {

            System.out.printf("Tick Num: %d%n", i);

            Set<InputType> inputs;
            try {
                inputs = new HashSet<>(strategyTicks.get(i).correctInputs);
            } catch (Exception e) {
                inputs = new HashSet<>(lastInputs);
            }

            Float yaw = null;

            if (i > jumpTick45Indices.get(0)) {
                inputs.remove(InputType.A);
                inputs.remove(InputType.D);
                yaw = jumpAngle;
            }

            updateSim(sim1, new UpdateSimOptions(
                    inputs.contains(InputType.W),
                    inputs.contains(InputType.A),
                    inputs.contains(InputType.S),
                    inputs.contains(InputType.D),
                    inputs.contains(InputType.SPR),
                    inputs.contains(InputType.SNK),
                    inputs.contains(InputType.AIR),
                    yaw));


            if (i+1 == lastJumpIndex) {
                jumpPos = getBlockStandingOn(sim1);
                if (jumpPos == null) {
                    setAllLabels("\u00A74False Strategy");
                    falseStrategy = true;
                    return;
                }
            }



            if (i >= lastJumpIndex) {
                landingPos = getHorizontalCollisionBlock(sim1);
                if (landingPos != null) {
                    break;
                }
            }
        }

        if (landingPos == null) {
            setAllLabels("\u00A74Can't Find LB");
            return;
        }

        // Simulate 45s
        PlayerSim sim2 = createSim();
        updateSim(sim2, null);

        P45OffsetDrawer.tasks.forEach(task -> task.cancel(false));
        P45OffsetDrawer.tasks.clear();
        P45OffsetDrawer.locPairs.clear();

        boolean landed = false;
        for (int i = 0; i < lastJumpIndex + 100; i++) {

            System.out.printf("Tick Num: %d%n", i);

            // === Get inputs to simulate for this tick ===
            Set<InputType> inputs;
            try {
                inputs = new HashSet<>(strategyTicks.get(i).correctInputs);
            } catch (Exception e) {
                inputs = new HashSet<>(lastInputs);
            }

            // === Determine yaw for this tick ===
            boolean isYawNull =  i < jumpTick45Indices.get(0) || (i == jumpTick45Indices.get(0) && !applyJAToFirst);
            float nonNullYaw = jumpTick45Indices.contains(i) ? jumpAngle : jumpAngle + (P45OffsetConfig.fortyFiveKey.equals("A") ? 45 : -45);
            Float yaw = isYawNull ? null : nonNullYaw;

            // Update sim using inputs and yaw
            Vec3 oldPos = sim2.getPositionVector();
            AxisAlignedBB oldBox = sim2.getEntityBoundingBox();

            UpdateSimOptions options = new UpdateSimOptions(
                    inputs.contains(InputType.W),
                    inputs.contains(InputType.A),
                    inputs.contains(InputType.S),
                    inputs.contains(InputType.D),
                    inputs.contains(InputType.SPR),
                    inputs.contains(InputType.SNK),
                    inputs.contains(InputType.AIR),
                    yaw);

            updateSim(sim2, options);

            Vec3 newPos = sim2.getPositionVector();
            P45OffsetDrawer.locPairs.put(oldPos, newPos);

            System.out.printf(
                    "UpdateSimOptions{W=%s, A=%s, S=%s, D=%s, JUMP=%s, SPR=%s, SNK=%s, rotationYaw=%s}%n",
                    options.W, options.A, options.S, options.D,
                    options.JUMP, options.SPR, options.SNK,
                    options.rotationYaw
            );


            System.out.printf("Sim loc: %s%n", newPos);


            BlockPos blockStandingOn = getBlockStandingOn(sim2);

            if (i+1 == lastJumpIndex && blockStandingOn == null) {

                AxisAlignedBB playerBB = sim2.getEntityBoundingBox();

                IBlockState state = mc.theWorld.getBlockState(jumpPos);
                AxisAlignedBB blockBB = state.getBlock().getCollisionBoundingBox(mc.theWorld, jumpPos, state);
                if (blockBB == null) return;

                double offset = EnumFacing.fromAngle(jumpAngle).getAxis() == EnumFacing.Axis.X
                        ? Math.abs(playerBB.maxX - blockBB.minX)
                        : Math.abs(playerBB.maxZ - blockBB.minZ);

                setAllLabels(showOvershootAmount ? formatDp("\u00A7eOS by %dp", offset) : "\u00A7eOvershoot");

                break;

            }
            if (i > lastJumpIndex && landingPos.equals(blockStandingOn)) {
                landed = true;
                updateOffsetFromBox(oldBox);
                break;
            }

            extendedTicks.add(options);

        }

        if (!landed) {
            setAllLabels("\u00A74Missed LB");
        }

        P45OffsetDrawer.tasks.add(P45OffsetDrawer.scheduledExecutor.schedule(() -> mc.addScheduledTask(P45OffsetDrawer.locPairs::clear), 5, TimeUnit.SECONDS));

    }

    private static void setAllLabels(String msg) {

        autoLabel = autoLabel.substring(0, autoLabel.lastIndexOf(')') + 3) + msg;
        xLabel = xLabel.substring(0, xLabel.lastIndexOf(')') + 3) + msg;
        zLabel = zLabel.substring(0, zLabel.lastIndexOf(')') + 3) + msg;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || !ResetHandler.movedSinceReset || finished || isConfigInvalid() || StrategyRecorder.recording || landingPos == null) {
            if (StrategyRecorder.recording) setAllLabels("\u00A7bRecording Strategy...");
            else if (isConfigInvalid()) setAllLabels("\u00A74Invalid Config");
            else if (landingPos == null) setAllLabels("\u00A74Can't Find LB");
            return;
        }

        if (stopOnInputFail) {
            Set<InputType> inputs = getInputs();

            Set<InputType> correctInputs;
            try {
                correctInputs = new HashSet<>(strategyTicks.get(tickNum).correctInputs);
            } catch (Exception e) {
                correctInputs = new HashSet<>(strategyTicks.get(strategyTicks.size()-1).correctInputs);
            }

            if (!inputs.equals(correctInputs)) {
                setAllLabels("\u00A7cFailed Inputs");
                finished = true;
                return;
            }
        }

        PlayerSim sim = createSim();

        // Index for extendedTicks represents the updateOptions needed to update the sim TO THAT tickNum
        // so start one above the tickNum or else you will try to update to the position you are already at
        for (int i = tickNum + 1; i < extendedTicks.size(); i++) {
            updateSim(sim, extendedTicks.get(i));
        }

        updateOffsetFromBox(sim.getEntityBoundingBox());

        if (tickNum + 1 == extendedTicks.size()) { // If there was no update to sim this tick (meaning player was already at spot needed for offset comparison)
            finished = true;
            return;
        }

        tickNum++;

    }

    private static void updateOffsetFromBox(AxisAlignedBB playerBB) { // takes an entity's "getEntityBoundingBox()"
        AxisAlignedBB bb = mc.theWorld.getBlockState(landingPos).getBlock().getCollisionBoundingBox(mc.theWorld, landingPos, mc.theWorld.getBlockState(landingPos));
        if (bb == null) return;

        double minX = bb.minX;
        double maxX = bb.maxX;
        double minZ = bb.minZ;
        double maxZ = bb.maxZ;

        double xOffset = xLabel.contains("+)") ? playerBB.maxX - minX : maxX - playerBB.minX;
        double zOffset = zLabel.contains("+)") ? playerBB.maxZ - minZ : maxZ - playerBB.minZ;

        xLabel = xLabel.substring(0, xLabel.lastIndexOf(')') + 3) + formatOffset(xOffset);
        zLabel = zLabel.substring(0, zLabel.lastIndexOf(')') + 3) + formatOffset(zOffset);

        EnumFacing facing = EnumFacing.fromAngle(jumpAngle);
        autoLabel = (facing == EnumFacing.EAST || facing == EnumFacing.WEST) ? xLabel : zLabel;
    }

    private static String formatOffset(double offset) {

        String color = offset >= 0 ? "\u00A7a+" : "\u00A7c";
        double abs = Math.abs(offset);

        if (P45OffsetConfig.eNotation && abs > 0) {

            int exponent = (int) Math.floor(Math.log10(abs));

            if (exponent <= P45OffsetConfig.eNotationMaxExp) {
                double leadingNum = abs / Math.pow(10, exponent);
                if (offset < 0) leadingNum = -leadingNum;

                String leadingNumStr = String.format("%." + P45OffsetConfig.eNotationPrecision + "f", leadingNum);
                return color + leadingNumStr + "e" + exponent;
            }
        }

        return formatDp("%s%dp", color, offset);
    }

    public static boolean isConfigInvalid() {
        return !P45OffsetConfig.jumpAngle.isEmpty() && getJumpAngle() == null
                || StrategyTick.getJumpTick(numOf45s - 1) == null
                || !strategyTicks.get(strategyTicks.size()-1).correctInputs.contains(AIR);
    }

    private static BlockPos getHorizontalCollisionBlock(EntityPlayerSP player) {

        if (!player.isCollidedHorizontally) {
            return null;
        }

        AxisAlignedBB bb = player.getEntityBoundingBox();
        World world = player.worldObj;

        // expand slightly in X/Z to catch the wall you hit
        List<AxisAlignedBB> boxes = world.getCollidingBoundingBoxes(player, bb.expand(0.05, 0, 0.05));

        for (AxisAlignedBB box : boxes) {
            // ensure it's actually a horizontal collision (not floor/ceiling)
            if (box.maxY > bb.minY && box.minY < bb.maxY) {
                return new BlockPos(box.minX, box.minY, box.minZ);
            }
        }

        return null;
    }


}
