package com.bzzrg.burgmod.features.turnhelper;

import com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig;
import com.bzzrg.burgmod.modutils.resetting.ResetHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig.deltaYaws;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig.yawPoints;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color2;
import static com.bzzrg.burgmod.modutils.GeneralUtils.formatDp;

public class TurnHelperHandler {

    private static int tickNum = 0;
    public static boolean finished = true;
    public static Float resetYaw = null;
    public static List<Float> turnAccuracyPercents = new ArrayList<>();
    public static String turnAccuracyLabel = color1 + "Turn Accuracy: \u00A7r?";

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (TurnHelperConfig.enabled && TurnHelperConfig.showTurnAccuracy) {
            mc.fontRendererObj.drawStringWithShadow(turnAccuracyLabel, TurnHelperConfig.turnAccuracyLabelX, TurnHelperConfig.turnAccuracyLabelY, -1);
        }
    }

    public static List<Float> getYaws() {
        List<Float> result = new ArrayList<>();
        List<YawPoint> sortedYawPoints = new ArrayList<>(yawPoints);
        sortedYawPoints.sort(Comparator.comparingInt(yawPoint -> yawPoint.tickNum));

        if (deltaYaws && resetYaw == null) return result;

        float currentYaw = deltaYaws ? resetYaw : 0f;

        for (int i = 0; i < sortedYawPoints.size(); i++) {
            YawPoint yawPoint = sortedYawPoints.get(i);
            int nextAirtime = i + 1 < sortedYawPoints.size()
                    ? sortedYawPoints.get(i + 1).tickNum
                    : yawPoint.tickNum + 1;

            currentYaw = deltaYaws ? currentYaw + yawPoint.yaw : yawPoint.yaw;

            for (int tickNum = yawPoint.tickNum; tickNum < nextAirtime; tickNum++) {
                result.add(currentYaw);
            }
        }

        return result;
    }

    public static void onReset() {
        tickNum = 0;
        finished = false;
        resetYaw = mc.thePlayer.rotationYaw;
        turnAccuracyPercents.clear();
        TurnHelperDrawer.resetMoving();

        if (yawPoints.isEmpty()) {
            turnAccuracyLabel = color1 + "Turn Accuracy: \u00A74No Yaw Points";
        } else {
            turnAccuracyLabel = formatDp("%sTurn Accuracy: %s%dp%%", color1, color2, 100f);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || !ResetHandler.movedSinceReset || finished || yawPoints.isEmpty()) {
            return;
        }

        int firstYawTickNum = Collections.min(yawPoints, Comparator.comparingInt(p -> p.tickNum)).tickNum-1;
        int lastYawTickNum = Collections.max(yawPoints, Comparator.comparingInt(p -> p.tickNum)).tickNum-1;

        if (tickNum == firstYawTickNum-1) {
            TurnHelperDrawer.startMoving();
        }

        if (tickNum >= firstYawTickNum) {

            float idealYaw = getYaws().get(tickNum - firstYawTickNum);

            float diff = (idealYaw - mc.thePlayer.rotationYaw) % 360f;
            if (diff > 180f) diff -= 360f;
            if (diff < -180f) diff += 360f;
            float percent = Math.max(0f, (1f - (Math.abs(diff) / 45f)) * 100f);

            turnAccuracyPercents.add(percent);
            float mean = (float) turnAccuracyPercents.stream().mapToDouble(f -> f).average().orElse(0);
            turnAccuracyLabel = formatDp("%sTurn Accuracy: %s%dp%%", color1, color2, mean);

        }
        if (tickNum == lastYawTickNum) {
            finished = true;
            return;
        }

        tickNum++;

    }


}
