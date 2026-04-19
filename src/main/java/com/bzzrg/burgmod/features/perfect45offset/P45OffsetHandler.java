package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig;
import com.bzzrg.burgmod.features.strategy.InputType;
import com.bzzrg.burgmod.features.strategy.StrategyRecorder;
import com.bzzrg.burgmod.features.strategy.StrategyTick;
import com.bzzrg.burgmod.modutils.resetting.ResetHandler;
import com.bzzrg.burgmod.modutils.simulation.PlayerSim;
import com.bzzrg.burgmod.modutils.simulation.UpdateSimOptions;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig.strategyTicks;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.P45OffsetConfig.*;
import static com.bzzrg.burgmod.features.strategy.InputType.*;
import static com.bzzrg.burgmod.modutils.GeneralUtils.*;
import static com.bzzrg.burgmod.modutils.simulation.SimUtils.*;

public class P45OffsetHandler {

    private static int tickNum = 0;
    private static boolean finished = true;
    public static BlockPos landingPos = null;
    public static BlockPos jumpPos = null;
    private static final List<UpdateSimOptions> optionsToSimStrat = new ArrayList<>();

    public static float ja = 0;

    public static String autoLabel = color1 + "Perfect 45 Offset (?): \u00A7r?";
    public static String xLabel = color1 + "Perfect 45 Offset (X?): \u00A7r?";
    public static String zLabel = color1 + "Perfect 45 Offset (Z?): \u00A7r?";

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (P45OffsetConfig.enabled) {
            if (showAutoOffset) {

                if (StrategyRecorder.recording) {
                    mc.fontRendererObj.drawStringWithShadow(color1 + "Perfect 45 Offset (?): \u00A7bRecording Strategy...", P45OffsetConfig.autoLabelX, P45OffsetConfig.autoLabelY, -1);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(autoLabel, P45OffsetConfig.autoLabelX, P45OffsetConfig.autoLabelY, -1);
                }

            }
            if (showXOffset) {
                if (StrategyRecorder.recording) {
                    mc.fontRendererObj.drawStringWithShadow(color1 + "Perfect 45 Offset (X?): \u00A7bRecording Strategy...", P45OffsetConfig.xLabelX, P45OffsetConfig.xLabelY, -1);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(xLabel, P45OffsetConfig.xLabelX, P45OffsetConfig.xLabelY, -1);
                }
            }
            if (showZOffset) {
                if (StrategyRecorder.recording) {
                    mc.fontRendererObj.drawStringWithShadow(color1 + "Perfect 45 Offset (Z?): \u00A7bRecording Strategy...", P45OffsetConfig.zLabelX, P45OffsetConfig.zLabelY, -1);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(zLabel, P45OffsetConfig.zLabelX, P45OffsetConfig.zLabelY, -1);
                }
            }
        }
    }

    public static List<Integer> getInvalidStates() {
        List<Integer> invalidStates = new ArrayList<>();
        if (!P45OffsetConfig.jumpAngle.isEmpty() && getJumpAngle() == null) {
            invalidStates.add(0);
        }
        if (StrategyRecorder.recording) {
            invalidStates.add(1);
        }
        if (strategyTicks.isEmpty()) {
            invalidStates.add(2);
        }
        if (numOf45s > StrategyTick.getJumpIndices().size()) {
            invalidStates.add(3);
        } else {

            List<Integer> jump45Indices = getJump45Indices();

            if (getLast(jump45Indices) == getLast(strategyTicks).getIndex()) {
                invalidStates.add(4);
            } else {
                int i = jump45Indices.get(0) + 1;
                for (Set<InputType> validInputs : getValid45Ticks()) {
                    Set<InputType> inputs = strategyTicks.get(i).correctInputs;

                    if (!inputs.equals(validInputs)) {

                        Set<InputType> tapJumpTick = new HashSet<>(Arrays.asList(W, A, D, SPR, JMP));
                        Set<InputType> releaseJumpTick = new HashSet<>(Arrays.asList(W, SPR, JMP));

                        // if the user has inputted release jump ticks for their strategy instead of tap jump ticks, then its not invalid
                        if (validInputs.equals(tapJumpTick) && inputs.equals(releaseJumpTick)) {
                            continue;
                        }

                        invalidStates.add(4);
                        break;
                    }

                    i++;
                }
            }

        }



        return invalidStates;
    }

    public static void onReset() {

        tickNum = 0;
        finished = false;
        landingPos = null;
        jumpPos = null;
        optionsToSimStrat.clear();

        List<Integer> invalidStates = getInvalidStates();

        if (invalidStates.contains(0)) {
            bmChat("\u00A7cWARN: Your jump angle is invalid! Either leave it blank or input a valid number.");
            playErrorSound();
            setAllLabels("\u00A74Invalid JA");
            return;
        }

        if (invalidStates.contains(1)) return;

        if (invalidStates.contains(2)) {
            setAllLabels("\u00A74No Strategy Set");
            return;
        } else if (invalidStates.contains(3)) {
            bmChat("\u00A7cWARN: # of 45s inside perfect 45 offset config is more than # of jumps inside your strategy!");
            playErrorSound();
            setAllLabels("\u00A74Invalid Strategy");
            return;

        } else if (invalidStates.contains(4)) {
            IChatComponent msg = new ChatComponentText("\u00A71[BM] \u00A7cWARN: The 45 jumps from your strategy are inputted wrong! Use the Fix Strat 45s button to fix your 45 jumps or ");
            IChatComponent click = new ChatComponentText("\u00A7l\u00A7b[Click Here]");

            click.getChatStyle()
                    .setChatClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/bmfixstrat45s"
                    ))
                    .setChatHoverEvent(new net.minecraft.event.HoverEvent(
                            net.minecraft.event.HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentText("\u00A7eClick to fix your 45 jumps!")
                    ));

            msg.appendSibling(click);
            if (mc.thePlayer != null) mc.thePlayer.addChatMessage(msg);

            playErrorSound();
            setAllLabels("\u00A74Invalid Strategy");
            return;
        }



        ja = (((getJumpAngle() == null ? mc.thePlayer.rotationYaw : getJumpAngle()) + 180) % 360 + 360) % 360 - 180; // can be values -180 <= value < 180, 180 outputs -180 which is expected

        xLabel = String.format(color1 + "%s45 Offset (X%s): \u00A7r?", shortenLabels ? "P" : "Perfect ", ja <= 0 ? "+" : "-");
        zLabel = String.format(color1 + "%s45 Offset (Z%s): \u00A7r?", shortenLabels ? "P" : "Perfect ", (-90 <= ja && ja <= 90) ? "+" : "-");

        autoLabel = (EnumFacing.fromAngle(ja).getAxis() == EnumFacing.Axis.X) ? xLabel : zLabel;

        // Get needed variables
        List<Integer> jump45Indices = getJump45Indices();

        Integer lastJumpIndex = StrategyTick.getLastJumpIndex();
        if (lastJumpIndex == null) return; // never null in practice

        Set<InputType> lastInputs = getLast(strategyTicks).correctInputs;

        // Reset lines
        P45OffsetDrawer.tasks.forEach(task -> task.cancel(false));
        P45OffsetDrawer.tasks.clear();
        P45OffsetDrawer.jblbLineLocs.clear();
        P45OffsetDrawer.perfectLineLocs.clear();

        // Get jump pos and landing pos
        PlayerSim sim1 = createSim();
        stopSim(sim1);

        int calcOffsetTick = -1; // never -1 in practice
        for (int i = 0; i < lastJumpIndex + 100; i++) {

            Set<InputType> inputs;
            try {
                inputs = new HashSet<>(strategyTicks.get(i).correctInputs);
            } catch (Exception e) {
                inputs = new HashSet<>(lastInputs);
            }

            float yaw = mc.thePlayer.rotationYaw;

            if (i > jump45Indices.get(0)) {
                inputs.remove(InputType.A);
                inputs.remove(InputType.D);
                yaw = ja;
            }
            if (i == jump45Indices.get(0) && applyJAToFirst) {
                yaw = ja;
            }

            Vec3 oldPos = sim1.getPositionVector();

            updateSim(sim1, new UpdateSimOptions(
                    inputs.contains(InputType.W),
                    inputs.contains(InputType.A),
                    inputs.contains(InputType.S),
                    inputs.contains(InputType.D),
                    inputs.contains(InputType.SPR),
                    inputs.contains(InputType.SNK),
                    inputs.contains(InputType.JMP),
                    yaw));

            Vec3 newPos = sim1.getPositionVector();

            P45OffsetDrawer.jblbLineLocs.put(oldPos, newPos);

            if (i+1 == lastJumpIndex) {
                jumpPos = getBlockStandingOn(sim1);
                if (jumpPos == null) {
                    setAllLabels(String.format("\u00A74Can't Find JB (T%d)", lastJumpIndex));
                    P45OffsetDrawer.tasks.add(P45OffsetDrawer.scheduledExecutor.schedule(() -> mc.addScheduledTask(P45OffsetDrawer.jblbLineLocs::clear), 5, TimeUnit.SECONDS));
                    return;
                }
            }
            if (i >= lastJumpIndex) {
                landingPos = getHorizontalCollisionBlock(sim1);
                if (landingPos != null) {
                    calcOffsetTick = i-1;
                    break;
                }
            }
        }

        if (landingPos == null) {
            setAllLabels("\u00A74Can't Find LB");
            P45OffsetDrawer.tasks.add(P45OffsetDrawer.scheduledExecutor.schedule(() -> mc.addScheduledTask(P45OffsetDrawer.jblbLineLocs::clear), 5, TimeUnit.SECONDS));
            return;
        }

        // At this point, jb and lb were both found so jblb line is not needed, clear it
        P45OffsetDrawer.jblbLineLocs.clear();

        for (int i = 0; i <= calcOffsetTick; i++) {

            Set<InputType> inputs;
            try {
                inputs = new HashSet<>(strategyTicks.get(i).correctInputs);
            } catch (Exception e) {
                inputs = new HashSet<>(lastInputs);
            }

            boolean useCurrentYaw = i < jump45Indices.get(0) || (i == jump45Indices.get(0) && !applyJAToFirst);
            float nonCurrentYaw = jump45Indices.contains(i) ? ja : ja + (P45OffsetConfig.fortyFiveKey.equals("A") ? 45 : -45);
            Float yaw = useCurrentYaw ? mc.thePlayer.rotationYaw : nonCurrentYaw;

            optionsToSimStrat.add(new UpdateSimOptions(
                    inputs.contains(InputType.W),
                    inputs.contains(InputType.A),
                    inputs.contains(InputType.S),
                    inputs.contains(InputType.D),
                    inputs.contains(InputType.SPR),
                    inputs.contains(InputType.SNK),
                    inputs.contains(InputType.JMP),
                    yaw));
        }

        PlayerSim sim2 = createSim();
        stopSim(sim2);

        boolean overshot = false;
        for (int i = 0; i < optionsToSimStrat.size(); i++) {

            Vec3 oldPos = sim2.getPositionVector();
            AxisAlignedBB oldBB = sim2.getEntityBoundingBox();
            updateSim(sim2, optionsToSimStrat.get(i));
            Vec3 newPos = sim2.getPositionVector();
            P45OffsetDrawer.perfectLineLocs.put(oldPos, newPos);

            if (i+1 == lastJumpIndex && getBlockStandingOn(sim2) == null) {
                overshot = true;
                updateOvershootOffset(oldBB);
                break;
            }
        }

        if (!overshot) updateLandOffset(sim2.getEntityBoundingBox());

        P45OffsetDrawer.tasks.add(P45OffsetDrawer.scheduledExecutor.schedule(() -> mc.addScheduledTask(P45OffsetDrawer.perfectLineLocs::clear), 5, TimeUnit.SECONDS));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || !ResetHandler.movedSinceReset || finished || !getInvalidStates().isEmpty()
                || landingPos == null || jumpPos == null) {
            return;
        }

        if (stopOnInputFail) {
            Set<InputType> inputs = getInputs();

            Set<InputType> correctInputs;
            try {
                correctInputs = new HashSet<>(strategyTicks.get(tickNum).correctInputs);
            } catch (Exception e) {
                correctInputs = new HashSet<>(getLast(strategyTicks).correctInputs);
            }

            if (!inputs.equals(correctInputs)) {
                setAllLabels("\u00A7cFailed Inputs");
                finished = true;
                return;
            }
        }

        PlayerSim sim = createSim();

        Integer lastJumpTick = StrategyTick.getLastJumpIndex();
        if (lastJumpTick == null) return; // never null in practice

        // Index for optionsToSimStrat represents the updateOptions needed to update the sim TO THAT tickNum
        // so start one above the tickNum or else you will try to update to the position you are already at
        boolean overshot = false;
        for (int i = tickNum + 1; i < optionsToSimStrat.size(); i++) {

            AxisAlignedBB oldBB = sim.getEntityBoundingBox();
            updateSim(sim, optionsToSimStrat.get(i));

            if (i+1 == lastJumpTick && getBlockStandingOn(sim) == null) {
                overshot = true;
                updateOvershootOffset(oldBB);

                if (tickNum+2 == lastJumpTick) {
                    finished = true;
                    return;
                }

                break;
            }

        }

        if (!overshot) updateLandOffset(sim.getEntityBoundingBox());

        if (tickNum + 1 == optionsToSimStrat.size()) { // If there was no update to sim this tick (meaning player was already at spot needed for offset comparison)
            finished = true;
            return;
        }

        tickNum++;
    }

    public static void setAllLabels(String msg) {
        autoLabel = autoLabel.substring(0, autoLabel.indexOf(')') + 3) + msg;
        xLabel = xLabel.substring(0, xLabel.indexOf(')') + 3) + msg;
        zLabel = zLabel.substring(0, zLabel.indexOf(')') + 3) + msg;
    }

    private static void updateLandOffset(AxisAlignedBB playerBB) { // use .getEntityBoundingBox() to call this

        AxisAlignedBB lbBB = getCollisionBox(landingPos);
        if (lbBB == null) return;

        double xOffset = xLabel.contains("+)") ? playerBB.maxX - lbBB.minX : lbBB.maxX - playerBB.minX;
        double zOffset = zLabel.contains("+)") ? playerBB.maxZ - lbBB.minZ : lbBB.maxZ - playerBB.minZ;

        xLabel = formatDp("%s%s%dp", xLabel.substring(0, xLabel.indexOf(')') + 3), xOffset >= 0 ? "\u00A7a+" : "\u00A7c", xOffset);
        zLabel = formatDp("%s%s%dp", zLabel.substring(0, zLabel.indexOf(')') + 3), zOffset >= 0 ? "\u00A7a+" : "\u00A7c", zOffset);

        autoLabel = (EnumFacing.fromAngle(ja).getAxis() == EnumFacing.Axis.X) ? xLabel : zLabel;
    }

    public static void updateOvershootOffset(AxisAlignedBB playerBB) { // use .getEntityBoundingBox() to call this

        if (!showOvershootAmount) {
            setAllLabels("\u00A7eOvershoot");
            return;
        }

        AxisAlignedBB jumpBB = getCollisionBox(jumpPos);
        if (jumpBB == null) return;

        double xOffset = xLabel.contains("+)") ? playerBB.minX - jumpBB.maxX : jumpBB.minX - playerBB.maxX;
        double zOffset = zLabel.contains("+)") ? playerBB.minZ - jumpBB.maxZ : jumpBB.minZ - playerBB.maxZ;

        xLabel = formatDp("%s\u00A7eOS (%dp)", xLabel.substring(0, xLabel.indexOf(')') + 3), xOffset);
        zLabel = formatDp("%s\u00A7eOS (%dp)", zLabel.substring(0, zLabel.indexOf(')') + 3), zOffset);

        autoLabel = (EnumFacing.fromAngle(ja).getAxis() == EnumFacing.Axis.X) ? xLabel : zLabel;
    }

    private static BlockPos getHorizontalCollisionBlock(EntityPlayerSP player) {

        if (!player.isCollidedHorizontally) {
            return null;
        }

        AxisAlignedBB bb = player.getEntityBoundingBox();
        World world = player.worldObj;

        List<AxisAlignedBB> boxes = world.getCollidingBoundingBoxes(player, bb.expand(0.05, 0, 0.05));

        AxisAlignedBB best = null;
        double bestArea = 0.0;

        for (AxisAlignedBB box : boxes) {

            if (!(box.maxY > bb.minY && box.minY < bb.maxY)) continue;

            double overlapX = Math.min(bb.maxX, box.maxX) - Math.max(bb.minX, box.minX);
            double overlapY = Math.min(bb.maxY, box.maxY) - Math.max(bb.minY, box.minY);
            double overlapZ = Math.min(bb.maxZ, box.maxZ) - Math.max(bb.minZ, box.minZ);

            if (overlapY <= 0) continue;

            double dx = Math.min(Math.abs(bb.maxX - box.minX), Math.abs(bb.minX - box.maxX));
            double dz = Math.min(Math.abs(bb.maxZ - box.minZ), Math.abs(bb.minZ - box.maxZ));

            double area;

            if (dx < dz) {
                if (overlapZ <= 0) continue;
                area = overlapY * overlapZ; // YZ plane (east/west wall)
            } else {
                if (overlapX <= 0) continue;
                area = overlapX * overlapY; // XY plane (north/south wall)
            }

            if (area > bestArea) {
                bestArea = area;
                best = box;
            }
        }

        return best != null ? new BlockPos(best.minX, best.minY, best.minZ) : null;
    }

    public static List<Integer> getJump45Indices() {
        List<Integer> jumpIndices = StrategyTick.getJumpIndices();
        return jumpIndices.subList(jumpIndices.size() - numOf45s, jumpIndices.size());
    }

    public static List<Set<InputType>> getValid45Ticks() {
        List<Integer> jump45Indices = getJump45Indices();
        List<Set<InputType>> valid45Ticks = new ArrayList<>();

        for (int i = jump45Indices.get(0)+1; i < strategyTicks.size(); i++) {
            InputType strafe = P45OffsetConfig.fortyFiveKey.equals("A") ? A : D;
            if (jump45Indices.contains(i)) {
                valid45Ticks.add(new HashSet<>(Arrays.asList(W, A, D, SPR, JMP)));
            } else {
                valid45Ticks.add(new HashSet<>(Arrays.asList(W, strafe, SPR)));
            }

        }

        return valid45Ticks;
    }


}
