package com.bzzrg.burgmod.features.trajectory;

import com.bzzrg.burgmod.config.featureconfig.TrajectoryConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;

import static com.bzzrg.burgmod.BurgMod.mc;

public class TrajectoryHandler {

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent e) {
        EntityPlayerSP real = mc.thePlayer;
        if (!TrajectoryConfig.enabled || real == null) return;

        EntityPlayerSP sim = createSimulatedPlayer(real);

        for (int i = 0; i < TrajectoryConfig.tickLength; i++) {

            sim.rotationYaw = real.rotationYaw;
            sim.rotationPitch = real.rotationPitch;

            sim.moveForward = real.moveForward;
            sim.moveStrafing = real.moveStrafing;
            sim.jumpMovementFactor = real.jumpMovementFactor;

            sim.movementInput = new MovementInputFromOptions(mc.gameSettings);
            sim.movementInput.moveForward = real.movementInput.moveForward;
            sim.movementInput.moveStrafe = real.movementInput.moveStrafe;
            sim.movementInput.jump = real.movementInput.jump;
            sim.movementInput.sneak = real.movementInput.sneak;

            if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                sim.setSprinting(real.isSprinting());
            }

            sim.setJumping(real.movementInput.jump);


            Vec3 oldPos = new Vec3(sim.posX, sim.posY, sim.posZ);

            // Advance sim by one tick (can't use onUpdate because that creates particles that can't be removed, specifically water particles are hard to get rid of)
            sim.handleWaterMovement();
            sim.onLivingUpdate();

            Vec3 newPos = new Vec3(sim.posX, sim.posY, sim.posZ);

            drawLine(oldPos, newPos);

        }
    }

    private void drawLine(Vec3 pos1, Vec3 pos2) {

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        double lift = 0.02;
        Vec3 p1 = pos1.addVector(0, lift, 0);
        Vec3 p2 = pos2.addVector(0, lift, 0);

        // direction of segment
        Vec3 dir = p2.subtract(p1).normalize();

        // camera look vector
        Vec3 camLook = mc.thePlayer.getLookVec().normalize();

        // perpendicular vector to make thickness (billboard tube)
        Vec3 side = dir.crossProduct(camLook).normalize();

        double dx = p1.xCoord - mc.thePlayer.posX;
        double dy = p1.yCoord - mc.thePlayer.posY;
        double dz = p1.zCoord - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);

        double halfWidth = TrajectoryConfig.thickness / (1.0 + dist * 0.15); // tube thickness

        Vec3 off = new Vec3(
                side.xCoord * halfWidth,
                side.yCoord * halfWidth,
                side.zCoord * halfWidth
        );

        Vec3 v1 = p1.add(off);
        Vec3 v2 = p1.subtract(off);
        Vec3 v3 = p2.subtract(off);
        Vec3 v4 = p2.add(off);

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE,
                GL11.GL_ZERO
        );
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);

        GlStateManager.color(TrajectoryConfig.colorRed, TrajectoryConfig.colorGreen, TrajectoryConfig.colorBlue, TrajectoryConfig.alpha);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        wr.pos(v1.xCoord - camX, v1.yCoord - camY, v1.zCoord - camZ).endVertex();
        wr.pos(v2.xCoord - camX, v2.yCoord - camY, v2.zCoord - camZ).endVertex();
        wr.pos(v3.xCoord - camX, v3.yCoord - camY, v3.zCoord - camZ).endVertex();
        wr.pos(v4.xCoord - camX, v4.yCoord - camY, v4.zCoord - camZ).endVertex();

        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }




    private EntityPlayerSP createSimulatedPlayer(EntityPlayerSP real) {
        EntityPlayerSP sim = new EntityPlayerSP(mc, real.worldObj, real.sendQueue, real.getStatFileWriter()) {
            @Override
            public void playSound(String name, float volume, float pitch) {}
            @Override
            public void spawnRunningParticles() {}
        };

        sim.copyLocationAndAnglesFrom(real);

        sim.prevPosX = real.prevPosX;
        sim.prevPosY = real.prevPosY;
        sim.prevPosZ = real.prevPosZ;

        sim.lastTickPosX = real.lastTickPosX;
        sim.lastTickPosY = real.lastTickPosY;
        sim.lastTickPosZ = real.lastTickPosZ;

        sim.posX = real.posX;
        sim.posY = real.posY;
        sim.posZ = real.posZ;

        sim.motionX = real.motionX;
        sim.motionY = real.motionY;
        sim.motionZ = real.motionZ;

        sim.onGround = real.onGround;
        sim.isCollidedHorizontally = real.isCollidedHorizontally;
        sim.isCollidedVertically = real.isCollidedVertically;
        sim.isCollided = real.isCollided;
        sim.isAirBorne = real.isAirBorne;

        sim.fallDistance = real.fallDistance;
        sim.stepHeight = real.stepHeight;

        sim.rotationYawHead = real.rotationYawHead;
        sim.renderYawOffset = real.renderYawOffset;
        sim.prevRotationYaw = real.prevRotationYaw;
        sim.prevRotationPitch = real.prevRotationPitch;

        sim.rotationYaw = real.rotationYaw;
        sim.rotationPitch = real.rotationPitch;

        sim.moveForward = real.moveForward;
        sim.moveStrafing = real.moveStrafing;
        sim.jumpMovementFactor = real.jumpMovementFactor;

        sim.movementInput = new MovementInputFromOptions(mc.gameSettings);
        sim.movementInput.moveForward = real.movementInput.moveForward;
        sim.movementInput.moveStrafe = real.movementInput.moveStrafe;
        sim.movementInput.jump = real.movementInput.jump;
        sim.movementInput.sneak = real.movementInput.sneak;

        sim.setSprinting(real.isSprinting());
        sim.setSneaking(real.isSneaking());
        sim.setJumping(real.movementInput.jump);

        sim.distanceWalkedModified = real.distanceWalkedModified;
        sim.distanceWalkedOnStepModified = real.distanceWalkedOnStepModified;

        for (PotionEffect effect : real.getActivePotionEffects()) {
            sim.addPotionEffect(new PotionEffect(effect));
        }

        try {
            // EntityPlayerSP.sprintToggleTimer (int)
            {
                final String MCP = "sprintToggleTimer";
                final String OBF = "field_71156_d"; // TODO: fill from EntityPlayerSP
                int v = ReflectionHelper.getPrivateValue(EntityPlayerSP.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, sim, v, MCP, OBF);
            }

            // EntityLivingBase.jumpTicks (int)
            {
                final String MCP = "jumpTicks";
                final String OBF = "field_70773_bE"; // TODO: fill from EntityLivingBase
                int v = ReflectionHelper.getPrivateValue(EntityLivingBase.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, sim, v, MCP, OBF);
            }

            // EntityLivingBase.landMovementFactor (float)
            {
                final String MCP = "landMovementFactor";
                final String OBF = "field_70746_aG"; // TODO: fill from EntityLivingBase
                float v = ReflectionHelper.getPrivateValue(EntityLivingBase.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, sim, v, MCP, OBF);
            }

            // EntityPlayer.speedInAir (float)
            {
                final String MCP = "speedInAir";
                final String OBF = "field_71102_ce"; // TODO: fill from EntityPlayer
                float v = ReflectionHelper.getPrivateValue(EntityPlayer.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityPlayer.class, sim, v, MCP, OBF);
            }

            // Entity.inWater (boolean)
            {
                final String MCP = "inWater";
                final String OBF = "field_70171_ac"; // TODO: verify in Entity
                boolean v = ReflectionHelper.getPrivateValue(Entity.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(Entity.class, sim, v, MCP, OBF);
            }

            // Entity.nextStepDistance (int)
            {
                final String MCP = "nextStepDistance";
                final String OBF = "field_70150_b"; // TODO: verify in Entity
                int v = ReflectionHelper.getPrivateValue(Entity.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(Entity.class, sim, v, MCP, OBF);
            }




        } catch (Throwable t) {
            t.printStackTrace();
        }

        return sim;
    }

}