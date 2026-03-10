package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.basicconfig.Perfect45OffsetConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.utils.resetting.ResetHandler;
import com.bzzrg.burgmod.utils.simulation.UpdateSimOptions;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.basicconfig.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.basicconfig.Perfect45OffsetConfig.*;
import static com.bzzrg.burgmod.config.specialconfig.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.features.strategy.StrategyPreviewer.lineLocPairs;
import static com.bzzrg.burgmod.features.strategy.StrategyPreviewer.scheduledExecutor;
import static com.bzzrg.burgmod.utils.GeneralUtils.formatDp;
import static com.bzzrg.burgmod.utils.GeneralUtils.getInputs;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.createPlayerSim;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.updateSim;

public class Perfect45OffsetLabel {

    private static int tickNum = 0;
    private static boolean finished = false;
    private static BlockPos landingPos = null;
    private static float f3JumpAngle = 0;
    private static final List<UpdateSimOptions> extendedTicks = new ArrayList<>();

    public static String autoLabel = "Placeholder";
    public static String xLabel = "Placeholder";
    public static String zLabel = "Placeholder";

    public static BlockPos getBlockPosStandingOn(EntityPlayerSP player) {
        BlockPos pos = new BlockPos(player.posX, player.getEntityBoundingBox().minY - 1E-4, player.posZ);
        if (player.worldObj.isAirBlock(pos)) return null;
        return pos;
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (Perfect45OffsetConfig.enabled) {
            if (Perfect45OffsetConfig.labelsShown.equals("AUTO")) {
                mc.fontRendererObj.drawStringWithShadow(autoLabel, Perfect45OffsetConfig.autoLabelX, Perfect45OffsetConfig.autoLabelY, 0xFFFFFF);
            } else {
                mc.fontRendererObj.drawStringWithShadow(xLabel, Perfect45OffsetConfig.xLabelX, Perfect45OffsetConfig.xLabelY, 0xFFFFFF);
                mc.fontRendererObj.drawStringWithShadow(zLabel, Perfect45OffsetConfig.zLabelX, Perfect45OffsetConfig.zLabelY, 0xFFFFFF);
            }
        }
    }

    public static void onReset() {

        tickNum = 0;
        finished = false;
        landingPos = null;
        extendedTicks.clear();

        f3JumpAngle = (((getJumpAngle() == null ? mc.thePlayer.rotationYaw : getJumpAngle()) + 180) % 360 + 360) % 360 - 180; // can be values -180 <= value < 180, 180 outputs -180 which is expected

        xLabel = String.format(color1 + "%s45 Offset (X%s): \u00A74Unloaded", shortenLabel ? "P" : "Perfect ", f3JumpAngle <= 0 ? "+" : "-");
        zLabel = String.format(color1 + "%s45 Offset (Z%s): \u00A74Unloaded", shortenLabel ? "P" : "Perfect ", (-90 <= f3JumpAngle && f3JumpAngle <= 90) ? "+" : "-");

        EnumFacing facing = EnumFacing.fromAngle(f3JumpAngle);
        autoLabel = (facing == EnumFacing.EAST || facing == EnumFacing.WEST) ? xLabel : zLabel;

        if (Perfect45OffsetConfig.isConfigInvalid()) return;

        List<Integer> jumpTickIndices = new ArrayList<>();
        for (int i = 0;; i++) {
            StrategyTick jumpTick = StrategyTick.getJumpTick(i);
            if (jumpTick == null) {
                break;
            } else {
                jumpTickIndices.add(jumpTick.getTickNum());
            }
        }
        Collections.reverse(jumpTickIndices);

        List<Integer> jumpTick45Indices = new ArrayList<>();
        for (int i = jumpTickIndices.size()-numOf45s; i < jumpTickIndices.size(); i++) {
            jumpTick45Indices.add(jumpTickIndices.get(i));
        }

        Set<InputType> lastInputs = strategyTicks.get(strategyTicks.size()-1).correctInputs;
        EntityPlayerSP sim = createPlayerSim(mc.thePlayer);

        for (int i = 0; i < 200; i++) {

            Set<InputType> inputs;
            try {
                inputs = new HashSet<>(strategyTicks.get(i).correctInputs);
            } catch (Exception e) {
                inputs = new HashSet<>(lastInputs);
            }

            Float yaw;
            if (jumpTickIndices.contains(i)) { // if its a jump tick, use either jump angle or just player facing
                yaw = getJumpAngle();

            } else if (i > jumpTick45Indices.get(0)) { // if it is a tick after a 45 jump
                yaw = f3JumpAngle + (Perfect45OffsetConfig.fortyFiveKey.equals("A") ? 45 : -45);

            } else { // if its not a jump tick or a tick after a 45 jump
                yaw = null;
            }

            AxisAlignedBB oldBox = sim.getEntityBoundingBox();

            Vec3 oldPos = sim.getPositionVector();

            UpdateSimOptions options = new UpdateSimOptions(
                    inputs.contains(InputType.W),
                    inputs.contains(InputType.A),
                    inputs.contains(InputType.S),
                    inputs.contains(InputType.D),
                    inputs.contains(InputType.SPR),
                    inputs.contains(InputType.SNK),
                    jumpTickIndices.contains(i),
                    yaw);

            updateSim(sim, options);
            System.out.printf(
                    "tick=%d, W=%s, A=%s, S=%s, D=%s, JUMP=%s, SPR=%s, SNK=%s, rotationYaw=%s%n",
                    i,
                    options.W,
                    options.A,
                    options.S,
                    options.D,
                    options.JUMP,
                    options.SPR,
                    options.SNK,
                    options.rotationYaw
            );
            lineLocPairs.put(oldPos, sim.getPositionVector());

            if (sim.onGround && i > jumpTick45Indices.get(jumpTick45Indices.size()-1)) {
                landingPos = getBlockPosStandingOn(sim);
                if (landingPos != null) {
                    updateLabelsFromPos(oldBox);
                }
                break;
            }

            extendedTicks.add(options);

        }

        scheduledExecutor.schedule(() -> mc.addScheduledTask(lineLocPairs::clear), 4, TimeUnit.SECONDS);

    }

    private static void setAllLabels(String msg) {

        autoLabel = autoLabel.substring(0, autoLabel.lastIndexOf(')') + 3) + msg;
        xLabel = xLabel.substring(0, xLabel.lastIndexOf(')') + 3) + msg;
        zLabel = zLabel.substring(0, zLabel.lastIndexOf(')') + 3) + msg;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        lineLocPairs.clear();

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || !ResetHandler.movedSinceReset || finished || Perfect45OffsetConfig.isConfigInvalid() || StrategyRecorder.recording || landingPos == null) {
            if (StrategyRecorder.recording) setAllLabels("\u00A7bRecording Strategy...");
            else if (Perfect45OffsetConfig.isConfigInvalid()) setAllLabels("\u00A74Invalid Config");
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

        EntityPlayerSP sim = createPlayerSim(mc.thePlayer);
        System.out.printf("tick: %d, sim.onground: %s, real.onground: %s%n", tickNum, sim.onGround, mc.thePlayer.onGround);

        // Index for extendedTicks represents the updateOptions needed to update the sim TO THAT tickNum
        // so start one above the tickNum or else you will try to update to the position you are already at
        for (int i = tickNum + 1; i < extendedTicks.size(); i++) {
            Vec3 oldPos = sim.getPositionVector();
            updateSim(sim, extendedTicks.get(i));
            lineLocPairs.put(oldPos, sim.getPositionVector());
        }

        updateLabelsFromPos(sim.getEntityBoundingBox());

        if (tickNum + 1 == extendedTicks.size()) { // If there was no update to sim this tick (meaning player was already at spot needed for offset comparison)
            finished = true;
            return;
        }

        tickNum++;

    }

    private static void updateLabelsFromPos(AxisAlignedBB playerBB) {
        AxisAlignedBB bb = mc.theWorld.getBlockState(landingPos).getBlock().getCollisionBoundingBox(mc.theWorld, landingPos, mc.theWorld.getBlockState(landingPos));
        if (bb == null) return;

        double minX = bb.minX;
        double maxX = bb.maxX;
        double minZ = bb.minZ;
        double maxZ = bb.maxZ;

        double xOffset = xLabel.contains("+)") ? playerBB.maxX - minX : maxX - playerBB.minX;
        double zOffset = zLabel.contains("+)") ? playerBB.maxZ - minZ : maxZ - playerBB.minZ;

        xLabel = formatDp(xLabel.substring(0, xLabel.lastIndexOf(')') + 3) + "%s%dp", xOffset >= 0 ? "\u00A7a+" : "\u00A7c", xOffset);
        zLabel = formatDp(zLabel.substring(0, zLabel.lastIndexOf(')') + 3) + "%s%dp", zOffset >= 0 ? "\u00A7a+" : "\u00A7c", zOffset);

        EnumFacing facing = EnumFacing.fromAngle(f3JumpAngle);
        autoLabel = (facing == EnumFacing.EAST || facing == EnumFacing.WEST) ? xLabel : zLabel;
    }

}
