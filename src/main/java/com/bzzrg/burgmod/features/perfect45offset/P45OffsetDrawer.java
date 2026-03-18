package com.bzzrg.burgmod.features.perfect45offset;

import com.bzzrg.burgmod.config.basicconfig.P45OffsetConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.utils.simulation.SimUtils.drawLine;

public class P45OffsetDrawer {

    public static final Map<Vec3, Vec3> locPairs = new HashMap<>();
    public static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    public static final List<ScheduledFuture<?>> tasks = new ArrayList<>();

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        if (!P45OffsetConfig.enabled) return;

        // Draw trajectory
        if (P45OffsetConfig.trajectoryOnReset) {
            for (Map.Entry<Vec3, Vec3> entry : locPairs.entrySet()) {
                drawLine(entry.getKey(), entry.getValue(), 0.7f, 0, 0, 0.3f, 0.1f);
            }
        }

        // Draw landing block
        BlockPos pos = P45OffsetLabel.landingPos;
        if (P45OffsetConfig.highlightLB && pos != null) {
            double camX = mc.getRenderManager().viewerPosX;
            double camY = mc.getRenderManager().viewerPosY;
            double camZ = mc.getRenderManager().viewerPosZ;

            double e = 0.002;
            AxisAlignedBB box = new AxisAlignedBB(
                    pos.getX() - e, pos.getY() - e, pos.getZ() - e,
                    pos.getX() + 1 + e, pos.getY() + 1 + e, pos.getZ() + 1 + e
            ).offset(-camX, -camY, -camZ);

            double minX = box.minX, minY = box.minY, minZ = box.minZ;
            double maxX = box.maxX, maxY = box.maxY, maxZ = box.maxZ;

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

            /* faces */

            GlStateManager.depthMask(false);
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

            /* outline */

            GlStateManager.depthMask(true);
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

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

}
