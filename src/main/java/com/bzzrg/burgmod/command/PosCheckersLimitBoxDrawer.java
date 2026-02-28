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

        double cameraX = mc.getRenderManager().viewerPosX;
        double cameraY = mc.getRenderManager().viewerPosY;
        double cameraZ = mc.getRenderManager().viewerPosZ;

        double worldMinX = PosCheckersConfig.xMin;
        double worldMaxX = PosCheckersConfig.xMax;
        double worldMinZ = PosCheckersConfig.zMin;
        double worldMaxZ = PosCheckersConfig.zMax;

        double playerY = mc.thePlayer.posY;
        double worldMinY = playerY - 20.0;
        double worldMaxY = playerY + 20.0;

        AxisAlignedBB box = new AxisAlignedBB(worldMinX, worldMinY, worldMinZ, worldMaxX, worldMaxY, worldMaxZ)
                .offset(-cameraX, -cameraY, -cameraZ);

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        // Change these if you want different color/alpha
        GlStateManager.disableCull();
        GlStateManager.color(0f, 1f, 0f, 0.25f);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        double minX = box.minX, minY = box.minY, minZ = box.minZ;
        double maxX = box.maxX, maxY = box.maxY, maxZ = box.maxZ;

        // Bottom
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();

        // Top
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();

        // North
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, minY, minZ).endVertex();

        // South
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();

        // West
        worldRenderer.pos(minX, minY, minZ).endVertex();
        worldRenderer.pos(minX, minY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, maxZ).endVertex();
        worldRenderer.pos(minX, maxY, minZ).endVertex();

        // East
        worldRenderer.pos(maxX, minY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, minZ).endVertex();
        worldRenderer.pos(maxX, maxY, maxZ).endVertex();
        worldRenderer.pos(maxX, minY, maxZ).endVertex();

        tessellator.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

}
