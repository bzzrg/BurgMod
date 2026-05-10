package com.bzzrg.burgmod.features.collisionoffset;

import com.bzzrg.burgmod.config.files.mainconfigsections.CollisionOffsetConfig;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color2;
import static com.bzzrg.burgmod.modutils.GeneralUtils.formatDp;
import static com.bzzrg.burgmod.modutils.GeneralUtils.getCollisionBox;

public class CollisionOffsetHandler {

    public static BlockPos xColBlock;
    public static BlockPos zColBlock;

    private static double xColOffset = 0;
    private static double zColOffset = 0;

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (CollisionOffsetConfig.enabled) {

            if (CollisionOffsetConfig.showXLabel) {

                String finalLabel;
                if (xColBlock == null) {
                    finalLabel = color1 + "X Col Offset: \u00A74X Col Unset";
                } else {
                    finalLabel = formatDp("%sX Col Offset: %s%dp", color1, color2, xColOffset);
                }
                mc.fontRendererObj.drawStringWithShadow(finalLabel, CollisionOffsetConfig.xLabelX, CollisionOffsetConfig.xLabelY, -1);

            }

            if (CollisionOffsetConfig.showZLabel) {
                String finalLabel;
                if (zColBlock == null) {
                    finalLabel = color1 + "Z Col Offset: \u00A74Z Col Unset";
                } else {
                    finalLabel = formatDp("%sZ Col Offset: %s%dp", color1, color2, zColOffset);
                }
                mc.fontRendererObj.drawStringWithShadow(finalLabel, CollisionOffsetConfig.zLabelX, CollisionOffsetConfig.zLabelY, -1);
            }

        }
    }

    private static boolean lastXOverlapping = false;
    private static boolean lastZOverlapping = false;
    private static AxisAlignedBB lastPlayerBB;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null) {
            return;
        }

        AxisAlignedBB playerBB = mc.thePlayer.getEntityBoundingBox();
        final double EPSILON = 1.0E-10;

        if (xColBlock != null) {

            AxisAlignedBB xColBB = getCollisionBox(xColBlock);

            if (xColBB != null) { // never null in practice, just to silence IDE

                boolean zOverlapping = !(playerBB.maxZ <= xColBB.minZ - EPSILON || xColBB.maxZ <= playerBB.minZ - EPSILON);

                if (zOverlapping) {

                    if (!lastZOverlapping) {
                        double offset = (playerBB.minX + playerBB.maxX) / 2d < (xColBB.minX + xColBB.maxX) / 2d ?
                                xColBB.minX - playerBB.maxX : playerBB.minX - xColBB.maxX;
                        if (offset < 1) xColOffset = offset;
                    }

                }

                lastZOverlapping = zOverlapping;
            }

        }

        if (zColBlock != null && lastPlayerBB != null) {

            AxisAlignedBB zColBB = getCollisionBox(zColBlock);

            if (zColBB != null) { // never null in practice, just to silence IDE

                boolean xOverlapping = !(playerBB.maxX <= zColBB.minX - EPSILON || zColBB.maxX <= playerBB.minX - EPSILON);

                if (xOverlapping) {

                    if (!lastXOverlapping) {
                        double offset = (lastPlayerBB.minZ + lastPlayerBB.maxZ) / 2d < (zColBB.minZ + zColBB.maxZ) / 2d ?
                                zColBB.minZ - lastPlayerBB.maxZ : lastPlayerBB.minZ - zColBB.maxZ;
                        if (offset < 1) zColOffset = offset;
                    }

                }

                lastXOverlapping = xOverlapping;
            }

        }

        lastPlayerBB = playerBB;

    }





}
