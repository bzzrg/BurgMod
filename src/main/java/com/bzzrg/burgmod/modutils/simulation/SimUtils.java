package com.bzzrg.burgmod.modutils.simulation;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.modutils.GeneralUtils.onGround;

public class SimUtils {

    private static class EmptyEffectRenderer extends EffectRenderer {
        public EmptyEffectRenderer(World world, TextureManager tex) {
            super(world, tex);
        }
        @Override
        public void addEffect(EntityFX effect) {}
    }

    public static void updateSim(PlayerSim sim, UpdateSimOptions options) { // options can be null, which means method just uses real player's "options"

        final String MCP = "pressed";
        final String OBF = "field_74513_e";
        boolean oldSprinting = ReflectionHelper.getPrivateValue(KeyBinding.class, mc.gameSettings.keyBindSprint, MCP, OBF);

        if (options != null) {
            if (options.W != null || options.S != null) {
                sim.movementInput.moveForward = 0f;
                if (Boolean.TRUE.equals(options.W)) sim.movementInput.moveForward += 1f;
                if (Boolean.TRUE.equals(options.S)) sim.movementInput.moveForward -= 1f;
                if (Boolean.TRUE.equals(options.SNK)) sim.movementInput.moveForward *= 0.3;
            }

            if (options.A != null || options.D != null) {
                sim.movementInput.moveStrafe = 0f;
                if (Boolean.TRUE.equals(options.A)) sim.movementInput.moveStrafe += 1f;
                if (Boolean.TRUE.equals(options.D)) sim.movementInput.moveStrafe -= 1f;
                if (Boolean.TRUE.equals(options.SNK)) sim.movementInput.moveStrafe *= 0.3;
            }

            if (options.SPR != null) {
                sim.setSprinting(options.SPR);
                ReflectionHelper.setPrivateValue(KeyBinding.class, mc.gameSettings.keyBindSprint, options.SPR, MCP, OBF);
            }
            if (options.SNK != null) {
                sim.movementInput.sneak = options.SNK;
                sim.setSneaking(options.SNK);
            }
            if (options.JUMP != null) {
                sim.movementInput.jump = options.JUMP;
                sim.setJumping(options.JUMP);
            }

            if (options.rotationYaw != null) sim.rotationYaw = options.rotationYaw;
        }

        EffectRenderer oldEffectRenderer = mc.effectRenderer;
        mc.effectRenderer = new EmptyEffectRenderer(mc.theWorld, mc.renderEngine);

        sim.onUpdate();

        mc.effectRenderer = oldEffectRenderer;

        ReflectionHelper.setPrivateValue(KeyBinding.class, mc.gameSettings.keyBindSprint, oldSprinting, MCP, OBF);
    }

    public static void stopSim(PlayerSim sim) {

        // Zero horizontal motion only
        sim.motionX = 0.0;
        sim.motionZ = 0.0;
        // leave motionY untouched

        // Zero movement input
        sim.moveForward = 0.0f;
        sim.moveStrafing = 0.0f;

        sim.movementInput.moveForward = 0.0f;
        sim.movementInput.moveStrafe = 0.0f;
        sim.movementInput.jump = false;
        sim.movementInput.sneak = false;

        // Stop actions
        sim.setSprinting(false);
        sim.setSneaking(false);
        sim.setJumping(false);

        // Ground state (only guaranteed truths)
        sim.onGround = true;
        sim.isAirBorne = false;

        // Reset fall
        sim.fallDistance = 0.0f;

        // Reset walk accumulation
        sim.distanceWalkedModified = 0.0f;
        sim.distanceWalkedOnStepModified = 0.0f;

        // Head follows body (safe)
        sim.rotationYawHead = sim.rotationYaw;

        // Reset jump delay
        try {
            final String MCP = "jumpTicks";
            final String OBF = "field_70773_bE";
            ReflectionHelper.setPrivateValue(EntityLivingBase.class, sim, 0, MCP, OBF);
        } catch (Throwable ignored) {}

        // Velocity flag
        sim.velocityChanged = false;
    }

    public static PlayerSim createSim() {
        EntityPlayerSP real = mc.thePlayer;

        PlayerSim sim = new PlayerSim();

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

        sim.setEntityBoundingBox(real.getEntityBoundingBox());

        sim.motionX = real.motionX;
        sim.motionY = real.motionY;
        sim.motionZ = real.motionZ;

        sim.onGround = onGround();
        sim.isCollidedHorizontally = real.isCollidedHorizontally;
        sim.isCollidedVertically = real.isCollidedVertically;
        sim.isCollided = real.isCollided;
        sim.isAirBorne = !onGround();

        sim.fallDistance = real.fallDistance;
        sim.stepHeight = real.stepHeight;

        sim.rotationYaw = real.rotationYaw;
        sim.rotationPitch = real.rotationPitch;
        sim.prevRotationYaw = real.prevRotationYaw;
        sim.prevRotationPitch = real.prevRotationPitch;
        sim.rotationYawHead = real.rotationYawHead;
        sim.renderYawOffset = real.renderYawOffset;

        sim.moveForward = real.moveForward;
        sim.moveStrafing = real.moveStrafing;
        sim.jumpMovementFactor = real.jumpMovementFactor;
        sim.setAIMoveSpeed(real.getAIMoveSpeed());

        sim.movementInput = new MovementInput();
        sim.movementInput.moveForward = real.movementInput.moveForward;
        sim.movementInput.moveStrafe = real.movementInput.moveStrafe;
        sim.movementInput.jump = real.movementInput.jump;
        sim.movementInput.sneak = real.movementInput.sneak;

        sim.setSprinting(real.isSprinting());
        sim.setSneaking(real.isSneaking());

        sim.distanceWalkedModified = real.distanceWalkedModified;
        sim.distanceWalkedOnStepModified = real.distanceWalkedOnStepModified;

        sim.capabilities = new PlayerCapabilities();
        sim.capabilities.allowFlying = real.capabilities.allowFlying;
        sim.capabilities.isCreativeMode = real.capabilities.isCreativeMode;
        sim.capabilities.isFlying = real.capabilities.isFlying;
        sim.capabilities.disableDamage = real.capabilities.disableDamage;
        sim.capabilities.allowEdit = real.capabilities.allowEdit;
        sim.capabilities.setFlySpeed(real.capabilities.getFlySpeed());
        sim.capabilities.setPlayerWalkSpeed(real.capabilities.getWalkSpeed());

        sim.velocityChanged = real.velocityChanged;

        for (PotionEffect effect : real.getActivePotionEffects()) {
            sim.addPotionEffect(new PotionEffect(effect));
        }

        for (IAttributeInstance realInst : real.getAttributeMap().getAllAttributes()) {
            IAttributeInstance simInst = sim.getAttributeMap().getAttributeInstance(realInst.getAttribute());

            if (simInst != null) {
                simInst.setBaseValue(realInst.getBaseValue());
                simInst.removeAllModifiers();

                for (AttributeModifier mod : realInst.func_111122_c()) {
                    simInst.applyModifier(mod);
                }
            }
        }
        try {
            {
                final String MCP = "isJumping";
                final String OBF = "field_70703_bu";
                boolean v = ReflectionHelper.getPrivateValue(EntityLivingBase.class, real, MCP, OBF);
                sim.setJumping(v);
            }

            {
                final String MCP = "sprintToggleTimer";
                final String OBF = "field_71156_d";
                int v = ReflectionHelper.getPrivateValue(EntityPlayerSP.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityPlayerSP.class, sim, v, MCP, OBF);
            }

            {
                final String MCP = "jumpTicks";
                final String OBF = "field_70773_bE";
                int v = ReflectionHelper.getPrivateValue(EntityLivingBase.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityLivingBase.class, sim, v, MCP, OBF);
            }

            {
                final String MCP = "speedInAir";
                final String OBF = "field_71102_ce";
                float v = ReflectionHelper.getPrivateValue(EntityPlayer.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(EntityPlayer.class, sim, v, MCP, OBF);
            }

            {
                final String MCP = "inWater";
                final String OBF = "field_70171_ac";
                boolean v = ReflectionHelper.getPrivateValue(Entity.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(Entity.class, sim, v, MCP, OBF);
            }

            {
                final String MCP = "nextStepDistance";
                final String OBF = "field_70150_b";
                int v = ReflectionHelper.getPrivateValue(Entity.class, real, MCP, OBF);
                ReflectionHelper.setPrivateValue(Entity.class, sim, v, MCP, OBF);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return sim;
    }

    public static void drawLine(Vec3 pos1, Vec3 pos2,
                                float colorRed, float colorGreen, float colorBlue,
                                float alpha, float thickness) {

        if (mc.thePlayer == null) return;

        double camX = mc.getRenderManager().viewerPosX;
        double camY = mc.getRenderManager().viewerPosY;
        double camZ = mc.getRenderManager().viewerPosZ;

        // Slight lift to avoid z-fighting with blocks
        Vec3 p1 = pos1.addVector(0, 0.02, 0);
        Vec3 p2 = pos2.addVector(0, 0.02, 0);

        Vec3 d = p2.subtract(p1);
        double len = d.lengthVector();
        if (len < 1e-6) return;
        Vec3 dir = d.normalize();

        // Pick a stable "up" that isn't parallel to dir, then build an orthonormal basis (u,v) around dir
        Vec3 up = (Math.abs(dir.yCoord) < 0.99) ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 u = dir.crossProduct(up).normalize();
        Vec3 v = dir.crossProduct(u).normalize();

        // Thickness scaling by distance (keep your existing behavior)
        double dx = p1.xCoord - mc.thePlayer.posX;
        double dy = p1.yCoord - mc.thePlayer.posY;
        double dz = p1.zCoord - mc.thePlayer.posZ;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double radius = (thickness / (1.0 + dist * 0.15)); // radius, not "halfWidth" of a quad

        // More segments = rounder tube (12 is decent, 16 is smoother, 24 is very smooth)
        final int segments = 24;

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                GL11.GL_ONE, GL11.GL_ZERO
        );
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();

        GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();

        // Cylinder sides
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (int i = 0; i < segments; i++) {
            double a0 = (i * (Math.PI * 2.0)) / segments;
            double a1 = ((i + 1) * (Math.PI * 2.0)) / segments;

            double c0 = Math.cos(a0), s0 = Math.sin(a0);
            double c1 = Math.cos(a1), s1 = Math.sin(a1);

            Vec3 r0 = new Vec3(
                    (u.xCoord * c0 + v.xCoord * s0) * radius,
                    (u.yCoord * c0 + v.yCoord * s0) * radius,
                    (u.zCoord * c0 + v.zCoord * s0) * radius
            );
            Vec3 r1 = new Vec3(
                    (u.xCoord * c1 + v.xCoord * s1) * radius,
                    (u.yCoord * c1 + v.yCoord * s1) * radius,
                    (u.zCoord * c1 + v.zCoord * s1) * radius
            );

            Vec3 p1a = p1.add(r0);
            Vec3 p1b = p1.add(r1);
            Vec3 p2b = p2.add(r1);
            Vec3 p2a = p2.add(r0);

            wr.pos(p1a.xCoord - camX, p1a.yCoord - camY, p1a.zCoord - camZ).endVertex();
            wr.pos(p1b.xCoord - camX, p1b.yCoord - camY, p1b.zCoord - camZ).endVertex();
            wr.pos(p2b.xCoord - camX, p2b.yCoord - camY, p2b.zCoord - camZ).endVertex();
            wr.pos(p2a.xCoord - camX, p2a.yCoord - camY, p2a.zCoord - camZ).endVertex();
        }

        tess.draw();

        // Optional: end caps (makes it look like a solid tube, not a hollow sleeve)
        wr.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);

        for (int i = 0; i < segments; i++) {
            double a0 = (i * (Math.PI * 2.0)) / segments;
            double a1 = ((i + 1) * (Math.PI * 2.0)) / segments;

            double c0 = Math.cos(a0), s0 = Math.sin(a0);
            double c1 = Math.cos(a1), s1 = Math.sin(a1);

            Vec3 r0 = new Vec3(
                    (u.xCoord * c0 + v.xCoord * s0) * radius,
                    (u.yCoord * c0 + v.yCoord * s0) * radius,
                    (u.zCoord * c0 + v.zCoord * s0) * radius
            );
            Vec3 r1 = new Vec3(
                    (u.xCoord * c1 + v.xCoord * s1) * radius,
                    (u.yCoord * c1 + v.yCoord * s1) * radius,
                    (u.zCoord * c1 + v.zCoord * s1) * radius
            );

            // Cap at p1 (winding one way)
            Vec3 c1v0 = p1.add(r0);
            Vec3 c1v1 = p1.add(r1);
            wr.pos(p1.xCoord - camX, p1.yCoord - camY, p1.zCoord - camZ).endVertex();
            wr.pos(c1v1.xCoord - camX, c1v1.yCoord - camY, c1v1.zCoord - camZ).endVertex();
            wr.pos(c1v0.xCoord - camX, c1v0.yCoord - camY, c1v0.zCoord - camZ).endVertex();

            // Cap at p2 (winding the opposite way)
            Vec3 c2v0 = p2.add(r0);
            Vec3 c2v1 = p2.add(r1);
            wr.pos(p2.xCoord - camX, p2.yCoord - camY, p2.zCoord - camZ).endVertex();
            wr.pos(c2v0.xCoord - camX, c2v0.yCoord - camY, c2v0.zCoord - camZ).endVertex();
            wr.pos(c2v1.xCoord - camX, c2v1.yCoord - camY, c2v1.zCoord - camZ).endVertex();
        }

        tess.draw();

        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

}
