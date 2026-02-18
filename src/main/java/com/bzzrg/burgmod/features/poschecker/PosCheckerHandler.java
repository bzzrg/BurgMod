package com.bzzrg.burgmod.features.poschecker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static com.bzzrg.burgmod.utils.PluginUtils.sendMessage;

public class PosCheckerHandler {
    public static final List<PosChecker> posCheckers = new ArrayList<>();

    public static class PosChecker {
        public final Axis axis;
        public final int ticksAfterJump;

        public PosChecker(Axis axis, int ticksAfterJump) {
            this.axis = axis;
            this.ticksAfterJump = ticksAfterJump;
        }


    }

    public enum Axis {
        X, Z, BOTH
    }
    public static class PosMessageSender {
        private final Axis axis;
        private final int ticksAfterJump;
        private int ticksLeft;

        public PosMessageSender(PosChecker posChecker) {
            this.axis = posChecker.axis;
            this.ticksAfterJump = posChecker.ticksAfterJump;
            this.ticksLeft = posChecker.ticksAfterJump;
        }

        public void tick() {
            ticksLeft--;
            if (ticksLeft <= 0) {

                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

                if (player != null) {
                    if (axis == Axis.X) {
                        sendMessage(String.format("\u00A71[BurgMod]\u00A7r X: %.5f \u00A77(T%d)", player.posX, ticksAfterJump));
                    } else if (axis == Axis.Z) {
                        sendMessage(String.format("\u00A71[BurgMod]\u00A7r Z: %.5f \u00A77(T%d)", player.posZ, ticksAfterJump));
                    } else if (axis == Axis.BOTH) {
                        sendMessage(String.format("\u00A71[BurgMod]\u00A7r X: %.5f, Z: %.5f \u00A77(T%d)", player.posX, player.posZ, ticksAfterJump));
                    }
                }

                posMessageSenders.remove(this);
            }
        }
    }

    private static boolean lastOnGround = true;
    private static final List<PosMessageSender> posMessageSenders = new ArrayList<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase != TickEvent.Phase.END || player == null) {
            return;
        }


        if (!player.onGround && lastOnGround) {
            for (PosChecker posChecker : posCheckers) {
                posMessageSenders.add(new PosMessageSender(posChecker));
            }
        }

        new ArrayList<>(posMessageSenders).forEach(PosMessageSender::tick);

        lastOnGround = player.onGround;


    }

}
