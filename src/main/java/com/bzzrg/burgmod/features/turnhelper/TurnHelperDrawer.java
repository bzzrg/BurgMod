package com.bzzrg.burgmod.features.turnhelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.jsonconfigfiles.TurnHelperConfig.*;
import static com.bzzrg.burgmod.features.turnhelper.TurnHelperHandler.getYaws;
import static com.bzzrg.burgmod.features.turnhelper.TurnHelperHandler.resetYaw;

public class TurnHelperDrawer {

    private static final double DRAW_DISTANCE = 4.0;
    private static final int ROWS = 28;
    private static final int SIDES = 18;
    private static final float YAW_OFFSET = 0.3f;

    private static final List<Float> unwrapped = new ArrayList<>();

    private static boolean queued = false;
    private static boolean moving = false;
    private static int currentIndex = 0;

    private static float renderYaw = 0f;
    private static float lastPartial = 0f;
    private static float currentTickYaw = 0f;
    private static float nextTickYaw = 0f;

    public static void startMoving() {
        List<Float> yaws = getYaws();
        if (mc.thePlayer == null || yaws.isEmpty() || resetYaw == null) return;

        unwrapped.clear();

        float prev = resetYaw;
        float acc = resetYaw;
        unwrapped.add(acc);

        for (float yaw : yaws) {
            float delta = MathHelper.wrapAngleTo180_float(yaw - prev);
            acc += delta;
            unwrapped.add(acc);
            prev = yaw;
        }

        renderYaw = resetYaw;
        currentTickYaw = resetYaw;
        nextTickYaw = resetYaw;
        lastPartial = 0f;
        currentIndex = 0;
        queued = true;
        moving = false;
    }

    public static void resetMoving() {
        queued = false;
        moving = false;
        currentIndex = 0;
        renderYaw = 0f;
        currentTickYaw = 0f;
        nextTickYaw = 0f;
        lastPartial = 0f;
        unwrapped.clear();
    }

    // IMPORTANT: LOWEST priority because startMoving() is called from another onClientTick,
    // and movement must begin on the next tick after that call.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || mc.thePlayer == null) return;

        if (queued) {
            queued = false;
            if (unwrapped.size() >= 2) {
                moving = true;
                currentIndex = 0;
                currentTickYaw = unwrapped.get(0);
                nextTickYaw = unwrapped.get(1);
                renderYaw = currentTickYaw;
            } else {
                moving = false;
                currentTickYaw = resetYaw;
                nextTickYaw = resetYaw;
                renderYaw = resetYaw;
            }
            lastPartial = 0f;

        } else if (moving) {
            if (currentIndex < unwrapped.size() - 1) {
                currentIndex++;
                currentTickYaw = unwrapped.get(currentIndex);
                if (currentIndex < unwrapped.size() - 1) {
                    nextTickYaw = unwrapped.get(currentIndex + 1);
                } else {
                    nextTickYaw = currentTickYaw;
                    moving = false;
                }
            } else {
                moving = false;
                currentTickYaw = unwrapped.get(unwrapped.size() - 1);
                nextTickYaw = currentTickYaw;
            }
            renderYaw = currentTickYaw;
            lastPartial = 0f;

        } else if (!unwrapped.isEmpty()) {
            currentTickYaw = unwrapped.get(unwrapped.size() - 1);
            nextTickYaw = currentTickYaw;
            renderYaw = currentTickYaw;
            lastPartial = 0f;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || !moving) return;

        float dt = event.renderTickTime - lastPartial;
        if (dt < 0.1f) return;

        float tickDelta = nextTickYaw - currentTickYaw;
        double smallestAngle = 1.2 * Math.pow((0.6 * mc.gameSettings.mouseSensitivity + 0.2), 3);
        double yawChange = smallestAngle * Math.round((tickDelta * dt) / smallestAngle);

        renderYaw += (float) yawChange;
        lastPartial = event.renderTickTime;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        if (!enabled || mc.thePlayer == null || mc.theWorld == null || yawPoints.isEmpty()) return;

        Vec3 pos = new Vec3(
                mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * e.partialTicks,
                mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * e.partialTicks,
                mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * e.partialTicks
        );

        boolean isDot = "DOT".equals(shape);

        if ("ALL_TARGETS_ON".equals(mode)) {
            List<Float> yaws = getYaws();
            List<Float> drawn = new ArrayList<>();
            for (float yaw : yaws) {
                if (!drawn.contains(yaw)) {
                    if (isDot) drawDot(pos, yaw);
                    else drawPillar(pos, yaw);
                    drawn.add(yaw);
                }
            }

        } else if ("ONE_MOVING_TARGET".equals(mode)) {
            if (resetYaw == null) return;

            float yaw = (moving || !unwrapped.isEmpty()) ? renderYaw : resetYaw;

            yaw %= 360f;
            if (yaw > 180f) yaw -= 360f;
            if (yaw < -180f) yaw += 360f;

            if (isDot) drawDot(pos, yaw);
            else drawPillar(pos, yaw);
        }
    }

    // Draws a screen-aligned circle at DRAW_DISTANCE in the direction of (yaw, playerPitch).
    private static void drawDot(Vec3 feet, float yaw) {
        float pitch = mc.thePlayer.rotationPitch;

        Vec3 eye = feet.addVector(0, mc.thePlayer.getEyeHeight(), 0);
        Vec3 dir = direction(yaw + YAW_OFFSET, pitch);
        Vec3 center = eye.addVector(dir.xCoord * DRAW_DISTANCE, dir.yCoord * DRAW_DISTANCE, dir.zCoord * DRAW_DISTANCE);

        Vec3 forward = direction(mc.thePlayer.rotationYaw, pitch).normalize();
        Vec3 worldUp = (Math.abs(pitch) > 85f) ? new Vec3(0, 0, pitch > 0 ? 1 : -1) : new Vec3(0, 1, 0);
        Vec3 right = forward.crossProduct(worldUp).normalize();
        Vec3 up = right.crossProduct(forward).normalize();

        double cx = mc.getRenderManager().viewerPosX;
        double cy = mc.getRenderManager().viewerPosY;
        double cz = mc.getRenderManager().viewerPosZ;

        int segments = 64;
        double step = Math.PI * 2 / segments;

        Tessellator t = Tessellator.getInstance();
        WorldRenderer w = t.getWorldRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.color(colorRed, colorGreen, colorBlue, opacity);

        w.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);

        w.pos(center.xCoord - cx, center.yCoord - cy, center.zCoord - cz).endVertex();

        for (int i = 0; i <= segments; i++) {
            double a = i * step;
            double cos = Math.cos(a);
            double sin = Math.sin(a);
            w.pos(
                    center.xCoord - cx + (right.xCoord * cos + up.xCoord * sin) * thickness,
                    center.yCoord - cy + (right.yCoord * cos + up.yCoord * sin) * thickness,
                    center.zCoord - cz + (right.zCoord * cos + up.zCoord * sin) * thickness
            ).endVertex();
        }

        t.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static void drawPillar(Vec3 feet, float yaw) {
        yaw += YAW_OFFSET;

        Vec3 eye = feet.addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0);

        Vec3[] mids = new Vec3[ROWS];
        Vec3[] halfs = new Vec3[ROWS];
        Vec3[][] rings = new Vec3[ROWS][SIDES];

        for (int i = 0; i < ROWS; i++) {
            double t = (double) i / (ROWS - 1);
            double pitch = (t * 2 - 1) * 90;

            Vec3 l = direction(yaw - yawPlusMinus, (float) pitch);
            Vec3 r = direction(yaw + yawPlusMinus, (float) pitch);

            Vec3 left  = eye.addVector(l.xCoord * DRAW_DISTANCE, l.yCoord * DRAW_DISTANCE, l.zCoord * DRAW_DISTANCE);
            Vec3 right = eye.addVector(r.xCoord * DRAW_DISTANCE, r.yCoord * DRAW_DISTANCE, r.zCoord * DRAW_DISTANCE);

            mids[i] = new Vec3(
                    (left.xCoord + right.xCoord) * 0.5,
                    (left.yCoord + right.yCoord) * 0.5,
                    (left.zCoord + right.zCoord) * 0.5
            );
            halfs[i] = new Vec3(
                    (right.xCoord - left.xCoord) * 0.5,
                    (right.yCoord - left.yCoord) * 0.5,
                    (right.zCoord - left.zCoord) * 0.5
            );
        }

        Vec3 forward = direction(yaw, 0);

        for (int i = 0; i < ROWS; i++) {
            Vec3 mid  = mids[i];
            Vec3 half = halfs[i];
            double len = half.lengthVector();

            if (len < 1e-6) {
                for (int s = 0; s < SIDES; s++) rings[i][s] = mid;
                continue;
            }

            Vec3 tangent = i == 0 ? mids[1].subtract(mids[0])
                    : i == ROWS - 1 ? mids[i].subtract(mids[i - 1])
                    : mids[i + 1].subtract(mids[i - 1]);

            if (tangent.lengthVector() < 1e-6) tangent = forward;

            Vec3 w = half.normalize();
            Vec3 d = tangent.crossProduct(w);
            if (d.lengthVector() < 1e-6) d = forward.crossProduct(w);
            d = d.normalize();

            double radius = Math.max(len * 0.28, 0.02);

            for (int s = 0; s < SIDES; s++) {
                double a = s * Math.PI * 2 / SIDES;
                rings[i][s] = mid.addVector(
                        w.xCoord * len * Math.cos(a) + d.xCoord * radius * Math.sin(a),
                        w.yCoord * len * Math.cos(a) + d.yCoord * radius * Math.sin(a),
                        w.zCoord * len * Math.cos(a) + d.zCoord * radius * Math.sin(a)
                );
            }
        }

        double cx = mc.getRenderManager().viewerPosX;
        double cy = mc.getRenderManager().viewerPosY;
        double cz = mc.getRenderManager().viewerPosZ;

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.color(colorRed, colorGreen, colorBlue, opacity);

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (int i = 0; i < ROWS - 1; i++) {
            for (int s = 0; s < SIDES; s++) {
                int n = (s + 1) % SIDES;

                Vec3 a = rings[i][s];
                Vec3 b = rings[i][n];
                Vec3 c = rings[i + 1][n];
                Vec3 d = rings[i + 1][s];

                wr.pos(a.xCoord - cx, a.yCoord - cy, a.zCoord - cz).endVertex();
                wr.pos(b.xCoord - cx, b.yCoord - cy, b.zCoord - cz).endVertex();
                wr.pos(c.xCoord - cx, c.yCoord - cy, c.zCoord - cz).endVertex();
                wr.pos(d.xCoord - cx, d.yCoord - cy, d.zCoord - cz).endVertex();
            }
        }

        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private static Vec3 direction(float yaw, float pitch) {
        float cy = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sy = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cp = -MathHelper.cos(-pitch * 0.017453292F);
        float sp = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(sy * cp, sp, cy * cp).normalize();
    }
}