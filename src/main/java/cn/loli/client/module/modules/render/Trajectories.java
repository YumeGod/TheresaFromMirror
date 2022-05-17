package cn.loli.client.module.modules.render;

import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.rotation.RotationHook;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.ColorProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Trajectories extends Module {

    private final ArrayList<Vec3> positions = new ArrayList<>();

    public final ColorProperty color = new ColorProperty("Box-Color", new Color(239, 235, 235, 210));

    private static final NumberProperty<Float> lineWidth = new NumberProperty<>("Width", 5f, 0.1f, 10f , 0.1f);

    public Trajectories() {
        super("Trajectories", "You can see where your projectiles will hit", ModuleCategory.RENDER);
    }

    private final IEventListener<RenderEvent> onRender = event -> {
        this.positions.clear();
        ItemStack itemStack = mc.thePlayer.getCurrentEquippedItem();
        MovingObjectPosition m = null;
        if (itemStack != null && (itemStack.getItem() instanceof ItemSnowball || itemStack.getItem() instanceof ItemEgg || itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemEnderPearl)) {
            EntityLivingBase thrower = mc.thePlayer;
            float rotationYaw = RotationHook.prevYaw + (RotationHook.yaw - RotationHook.prevYaw) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            float rotationPitch = RotationHook.prevPitch + (RotationHook.pitch - RotationHook.prevPitch) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            double posX = thrower.lastTickPosX + (thrower.posX - thrower.lastTickPosX) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            double posY = thrower.lastTickPosY + thrower.getEyeHeight() + (thrower.posY - thrower.lastTickPosY) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            double posZ = thrower.lastTickPosZ + (thrower.posZ - thrower.lastTickPosZ) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
            posX -= (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            posY -= 0.10000000149011612D;
            posZ -= (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
            float multipicator = 0.4F;
            if (itemStack.getItem() instanceof ItemBow) {
                multipicator = 1;
            }
            double motionX = (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * multipicator);
            double motionZ = (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * multipicator);
            double motionY = (-MathHelper.sin((rotationPitch) / 180.0F * (float) Math.PI) * multipicator);

            double x = motionX;
            double y = motionY;
            double z = motionZ;
            float inaccuracy = 0;
            float velocity = 1.5F;
            if (itemStack.getItem() instanceof ItemBow) {
                int i = mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration() - mc.thePlayer.getItemInUseCount();
                float f = (float) i / 20.0F;
                f = (f * f + f * 2.0F) / 3.0F;

                if (f > 1.0F) {
                    f = 1.0F;
                }
                velocity = f * 2.0F * 1.5F;
            }

            Random rand = new Random();
            float ff = MathHelper.sqrt_double(x * x + y * y + z * z);
            x = x / (double) ff;
            y = y / (double) ff;
            z = z / (double) ff;
            x = x + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            y = y + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            z = z + rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
            x = x * (double) velocity;
            y = y * (double) velocity;
            z = z * (double) velocity;
            motionX = x;
            motionY = y;
            motionZ = z;
            float prevRotationYaw = (float) (MathHelper.atan2(x, z) * 180.0D / Math.PI);
            float prevRotationPitch = (float) (MathHelper.atan2(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D / Math.PI);

            boolean b = true;
            int ticksInAir = 0;
            while (b) {
                if (ticksInAir > 300) {
                    b = false;
                }
                ticksInAir++;
                Vec3 vec3 = new Vec3(posX, posY, posZ);
                Vec3 vec31 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                MovingObjectPosition movingobjectposition = mc.theWorld.rayTraceBlocks(vec3, vec31);
                vec3 = new Vec3(posX, posY, posZ);
                vec31 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (movingobjectposition != null) {
                    vec31 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
                }
                for (Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity != mc.thePlayer && entity instanceof EntityLivingBase) {
                        float f = 0.3F;
                        AxisAlignedBB localAxisAlignedBB = entity.getEntityBoundingBox().expand(f, f, f);
                        MovingObjectPosition localMovingObjectPosition = localAxisAlignedBB.calculateIntercept(vec3, vec31);
                        if (localMovingObjectPosition != null) {
                            movingobjectposition = localMovingObjectPosition;
                            break;
                        }
                    }
                }
                if (movingobjectposition != null) {
                    b = false;
                }
                m = movingobjectposition;

                posX += motionX;
                posY += motionY;
                posZ += motionZ;

                float f1 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
                rotationYaw = (float) (MathHelper.atan2(motionX, motionZ) * 180.0D / Math.PI);

                for (rotationPitch = (float) (MathHelper.atan2(motionY, f1) * 180.0D / Math.PI); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)

                    while (rotationPitch - prevRotationPitch >= 180.0F) {
                        prevRotationPitch += 360.0F;
                    }

                while (rotationYaw - prevRotationYaw < -180.0F) {
                    prevRotationYaw -= 360.0F;
                }

                while (rotationYaw - prevRotationYaw >= 180.0F) {
                    prevRotationYaw += 360.0F;
                }
                float f2 = 0.99F;
                float f3 = 0.03F;
                if (itemStack.getItem() instanceof ItemBow) {
                    f3 = 0.05F;
                }
                motionX *= f2;
                motionY *= f2;
                motionZ *= f2;
                motionY -= f3;
                this.positions.add(new Vec3(posX, posY, posZ));
            }
            if (this.positions.size() > 1) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GlStateManager.disableCull();
                GL11.glDepthMask(false);
                Color color = this.color.getPropertyValue();
                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.7f);
                GL11.glLineWidth((float) (lineWidth.getPropertyValue() / 2f));
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                worldrenderer.begin(3, DefaultVertexFormats.POSITION);
                for (Vec3 vec3 : this.positions) {
                    worldrenderer.pos((float) vec3.xCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX(), (float) vec3.yCoord -
                            ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY(), (float) vec3.zCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ()).endVertex();
                }
                tessellator.draw();

                if (m != null) {
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.3f);
                    Vec3 hitVec = m.hitVec;
                    EnumFacing enumFacing1 = m.sideHit;
                    float minX = (float) (hitVec.xCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX());
                    float maxX = (float) (hitVec.xCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX());
                    float minY = (float) (hitVec.yCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY());
                    float maxY = (float) (hitVec.yCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY());
                    float minZ = (float) (hitVec.zCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ());
                    float maxZ = (float) (hitVec.zCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ());
                    if (enumFacing1 == EnumFacing.SOUTH) {
                        minX -= 0.4;
                        maxX += 0.4;
                        minY -= 0.4;
                        maxY += 0.4;
                        maxZ += 0.02;
                        minZ += 0.05;
                    } else if (enumFacing1 == EnumFacing.NORTH) {
                        minX -= 0.4;
                        maxX += 0.4;
                        minY -= 0.4;
                        maxY += 0.4;
                        maxZ -= 0.02;
                        minZ -= 0.05;
                    } else if (enumFacing1 == EnumFacing.EAST) {
                        maxX += 0.02;
                        minX += 0.05;
                        minY -= 0.4;
                        maxY += 0.4;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.WEST) {
                        maxX -= 0.02;
                        minX -= 0.05;
                        minY -= 0.4;
                        maxY += 0.4;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.UP) {
                        minX -= 0.4;
                        maxX += 0.4;
                        maxY += 0.02;
                        minY += 0.05;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    } else if (enumFacing1 == EnumFacing.DOWN) {
                        minX -= 0.4;
                        maxX += 0.4;
                        maxY -= 0.02;
                        minY -= 0.05;
                        minZ -= 0.4;
                        maxZ += 0.4;
                    }

                    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
                    //Side 1
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    //Side 2
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    //Side 3
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    //Side 4
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    //Bottom
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    //Top
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();

                    worldrenderer.endVertex();
                    tessellator.draw();
                    GL11.glLineWidth((float) 2);
                    worldrenderer.begin(3, DefaultVertexFormats.POSITION);

                    //Side 1
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(maxX, minY, minZ).endVertex();
                    worldrenderer.pos(maxX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, minY, maxZ).endVertex();
                    worldrenderer.pos(minX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, maxZ).endVertex();
                    worldrenderer.pos(maxX, maxY, minZ).endVertex();
                    worldrenderer.pos(minX, maxY, minZ).endVertex();

                    worldrenderer.endVertex();
                    tessellator.draw();
                }
                GL11.glLineWidth((float) 1);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glDepthMask(true);
                GlStateManager.enableCull();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }
        }
    };



}
