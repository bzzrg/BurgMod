package com.bzzrg.burgmod.features.distanceoffset;

import com.bzzrg.burgmod.config.files.mainconfigsections.DistanceOffsetConfig;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color1;
import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.color2;
import static com.bzzrg.burgmod.modutils.GeneralUtils.*;

public class DistanceOffsetHandler {

    private static BlockPos lb;
    private static BlockPos mmBlock;

    private static AxisAlignedBB jumpBB;

    private static double distanceOffset = 0;

    private static class CustomGuiChat extends GuiChat {

        public CustomGuiChat(String initText) {
            super(initText);
        }

        @Override
        public void sendChatMessage(String message) {
            super.sendChatMessage(message);

            BlockPos blockStandingOn = getBlockStandingOn(mc.thePlayer);
            if (blockStandingOn != null && getCollisionBox(blockStandingOn) == null) {
                blockStandingOn = null;
            }

            BlockPos blockLookingAt = getBlockLookingAt();

            String lower = message.toLowerCase();
            if (lower.startsWith("/cyv ") || lower.startsWith("/mpk ") || lower.startsWith("/bm ")) {
                String cmd = message.substring(message.indexOf(' ') + 1);
                switch (cmd) {
                    case "setlb":
                        if (blockStandingOn == null) {
                            bmChat("\u00A7cPlease stand on a valid block!");
                        } else {
                            lb = blockStandingOn;
                            bmChat("\u00A7aSet landing block to the block you are standing on!");
                        }
                        break;
                    case "setmm":
                        if (blockStandingOn == null) {
                            bmChat("\u00A7cPlease stand on a valid block!");
                        } else {
                            mmBlock = blockStandingOn;
                            bmChat("\u00A7aSet momentum block to the block you are standing on!");
                        }
                        break;
                    case "setlb target":
                        if (blockLookingAt == null) {
                            bmChat("\u00A7cPlease look at a valid block!");
                        } else {
                            lb = blockLookingAt;
                            bmChat("\u00A7aSet landing block to the block you are looking at!");
                        }
                        break;
                    case "setmm target":
                        if (blockLookingAt == null) {
                            bmChat("\u00A7cPlease look at a valid block!");
                        } else {
                            mmBlock = blockLookingAt;
                            bmChat("\u00A7aSet momentum block to the block you are looking at!");
                        }
                        break;
                }
            }
        }
    }



    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiChat && !(event.gui instanceof CustomGuiChat)) {

            final String MCP = "defaultInputFieldText";
            final String OBF = "field_146409_v";
            String defaultInputFieldText = ReflectionHelper.getPrivateValue(GuiChat.class, (GuiChat) event.gui, MCP, OBF);

            event.gui = new CustomGuiChat(defaultInputFieldText);
        }
    }


    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if (DistanceOffsetConfig.enabled) {

            String finalLabel;
            if (mmBlock == null) {
                finalLabel = color1 + "Distance Offset: \u00A74MM Block Unset";
            } else if (lb == null) {
                finalLabel = color1 + "Distance Offset: \u00A74LB Unset";
            } else {
                finalLabel = formatDp("%sDistance Offset: %s%dp", color1, color2, distanceOffset);
            }

            mc.fontRendererObj.drawStringWithShadow(finalLabel, DistanceOffsetConfig.labelX, DistanceOffsetConfig.labelY, -1);
        }
    }

    private static AxisAlignedBB lastPlayerBB;
    private static AxisAlignedBB nextLastPlayerBB;
    private static BlockPos lastBlockStandingOn;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || lb == null || mmBlock == null) {
            return;
        }

        if (jumpBB != null) {

            // These are never null in practice, != null checks are just to silence IDE
            AxisAlignedBB lbBB = getCollisionBox(lb);
            AxisAlignedBB mmBlockBB = getCollisionBox(mmBlock);

            if (lbBB != null && mmBlockBB != null && mc.thePlayer.posY - 1.0E-10 <= lbBB.maxY) {

                double mmOffset;
                double landOffset;
                if ("X".equals(DistanceOffsetConfig.axis)) {
                    mmOffset = mmBlock.getX() <= lb.getX() ? mmBlockBB.maxX -  jumpBB.minX : jumpBB.maxX - mmBlockBB.minX;
                    landOffset = mmBlock.getX() <= lb.getX() ? lastPlayerBB.maxX - lbBB.minX : lbBB.maxX - lastPlayerBB.minX;
                } else {
                    mmOffset = mmBlock.getZ() <= lb.getZ() ? mmBlockBB.maxZ -  jumpBB.minZ : jumpBB.maxZ - mmBlockBB.minZ;
                    landOffset = mmBlock.getZ() <= lb.getZ() ? lastPlayerBB.maxZ - lbBB.minZ : lbBB.maxZ - lastPlayerBB.minZ;
                }

                double offset = landOffset + mmOffset;

                if (landOffset >= -1) {
                    distanceOffset = offset;
                    jumpBB = null;

                    if (offset > 0) {

                        if (landOffset < 0) { // Didn't land the jump

                            if (DistanceOffsetConfig.titleWhenPositive) {
                                sendTitle(DistanceOffsetConfig.titleTextPositive.replace("&", "\u00A7"), "", 5, 40, 5);
                            }
                            if (DistanceOffsetConfig.soundWhenPositive) {
                                playSound("mob.enderdragon.growl", DistanceOffsetConfig.soundVolumePositive, 0.8f);
                            }

                        } else { // Did land the jump

                            if (DistanceOffsetConfig.titleWhenLand) {
                                sendTitle(DistanceOffsetConfig.titleTextLand.replace("&", "\u00A7"), "", 5, 40, 5);
                            }
                            if (DistanceOffsetConfig.soundWhenLand) {
                                playSound("random.levelup", DistanceOffsetConfig.soundVolumeLand, 0.6f);
                            }

                        }

                    }

                }


            }
        }

        BlockPos blockStandingOn = getBlockStandingOn(mc.thePlayer);
        if (mmBlock.equals(lastBlockStandingOn) && blockStandingOn == null) {
            jumpBB = nextLastPlayerBB;
        }

        nextLastPlayerBB = lastPlayerBB;
        lastPlayerBB = mc.thePlayer.getEntityBoundingBox();
        lastBlockStandingOn = blockStandingOn;
    }





}
