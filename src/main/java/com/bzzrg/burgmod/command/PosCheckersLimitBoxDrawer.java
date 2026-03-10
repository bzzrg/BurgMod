package com.bzzrg.burgmod.command;

import com.bzzrg.burgmod.config.specialconfig.PosCheckersConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.bzzrg.burgmod.BurgMod.mc;

public class PosCheckersLimitBoxDrawer {

    public static boolean enabled = false;
    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();

    public static void drawFor4Seconds() {
        scheduledTasks.forEach(task -> task.cancel(true));
        enabled = true;
        scheduledTasks.add(scheduledExecutor.schedule(() -> mc.addScheduledTask(() -> enabled = false), 4, TimeUnit.SECONDS));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        if (!enabled || mc.thePlayer == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        double minX = PosCheckersConfig.xMin;
        double maxX = PosCheckersConfig.xMax;
        double minZ = PosCheckersConfig.zMin;
        double maxZ = PosCheckersConfig.zMax;

        double playerY = mc.thePlayer.posY;
        double minY = playerY - 20.0;
        double maxY = playerY + 20.0;

        AxisAlignedBB box = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ)
                .offset(-camX, -camY, -camZ);

        minX = box.minX; minY = box.minY; minZ = box.minZ;
        maxX = box.maxX; maxY = box.maxY; maxZ = box.maxZ;

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
        );

        GlStateManager.disableCull();
        GlStateManager.enableDepth();

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        /* ---------- TRANSPARENT FACES ---------- */

        GlStateManager.depthMask(false);   // IMPORTANT: faces don't block lines
        GlStateManager.color(0f, 1f, 0f, 0.12f);

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        wr.pos(minX,minY,minZ).endVertex();
        wr.pos(maxX,minY,minZ).endVertex();
        wr.pos(maxX,minY,maxZ).endVertex();
        wr.pos(minX,minY,maxZ).endVertex();

        wr.pos(minX,maxY,minZ).endVertex();
        wr.pos(minX,maxY,maxZ).endVertex();
        wr.pos(maxX,maxY,maxZ).endVertex();
        wr.pos(maxX,maxY,minZ).endVertex();

        wr.pos(minX,minY,minZ).endVertex();
        wr.pos(minX,maxY,minZ).endVertex();
        wr.pos(maxX,maxY,minZ).endVertex();
        wr.pos(maxX,minY,minZ).endVertex();

        wr.pos(minX,minY,maxZ).endVertex();
        wr.pos(maxX,minY,maxZ).endVertex();
        wr.pos(maxX,maxY,maxZ).endVertex();
        wr.pos(minX,maxY,maxZ).endVertex();

        wr.pos(minX,minY,minZ).endVertex();
        wr.pos(minX,minY,maxZ).endVertex();
        wr.pos(minX,maxY,maxZ).endVertex();
        wr.pos(minX,maxY,minZ).endVertex();

        wr.pos(maxX,minY,minZ).endVertex();
        wr.pos(maxX,maxY,minZ).endVertex();
        wr.pos(maxX,maxY,maxZ).endVertex();
        wr.pos(maxX,minY,maxZ).endVertex();

        tess.draw();

        /* ---------- OUTLINE ---------- */

        GlStateManager.depthMask(true);    // restore depth writes for lines
        GL11.glLineWidth(2f);

        GlStateManager.color(0f, 1f, 0f, 1f);

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        wr.pos(minX,minY,minZ).endVertex(); wr.pos(maxX,minY,minZ).endVertex();
        wr.pos(maxX,minY,minZ).endVertex(); wr.pos(maxX,minY,maxZ).endVertex();
        wr.pos(maxX,minY,maxZ).endVertex(); wr.pos(minX,minY,maxZ).endVertex();
        wr.pos(minX,minY,maxZ).endVertex(); wr.pos(minX,minY,minZ).endVertex();

        wr.pos(minX,maxY,minZ).endVertex(); wr.pos(maxX,maxY,minZ).endVertex();
        wr.pos(maxX,maxY,minZ).endVertex(); wr.pos(maxX,maxY,maxZ).endVertex();
        wr.pos(maxX,maxY,maxZ).endVertex(); wr.pos(minX,maxY,maxZ).endVertex();
        wr.pos(minX,maxY,maxZ).endVertex(); wr.pos(minX,maxY,minZ).endVertex();

        wr.pos(minX,minY,minZ).endVertex(); wr.pos(minX,maxY,minZ).endVertex();
        wr.pos(maxX,minY,minZ).endVertex(); wr.pos(maxX,maxY,minZ).endVertex();
        wr.pos(maxX,minY,maxZ).endVertex(); wr.pos(maxX,maxY,maxZ).endVertex();
        wr.pos(minX,minY,maxZ).endVertex(); wr.pos(minX,maxY,maxZ).endVertex();

        tess.draw();

        /* ---------- RESTORE STATE ---------- */

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

}
