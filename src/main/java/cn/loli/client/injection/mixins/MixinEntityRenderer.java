

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.modules.combat.KeepSprint;
import cn.loli.client.module.modules.render.NoFov;
import cn.loli.client.module.modules.render.ViewClip;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import dev.xix.TheresaClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private float fovModifierHand;

    @Shadow
    private Entity pointedEntity;

    @Shadow
    private ShaderGroup theShaderGroup;

    @Shadow
    private boolean useShader;

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V"))
    private void onRenderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        TheresaClient.getInstance().getEventBus().call(new RenderWorldLastEvent(Minecraft.getMinecraft().renderGlobal, partialTicks));
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V"))
    private void onRenderHand(CallbackInfo ci) {
        TheresaClient.getInstance().getEventBus().call(new RenderEvent());
    }

    @Inject(method = "renderWorldPass", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableFog()V", shift = At.Shift.AFTER))
    private void eventRender3D(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        Render3DEvent eventRender = new Render3DEvent(pass, partialTicks, finishTimeNano);
        TheresaClient.getInstance().getEventBus().call(eventRender);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "updateFovModifierHand", at = @At("RETURN"))
    private void updateFovModifierHand(CallbackInfo callbackInfo) {
        if (Main.INSTANCE.moduleManager.getModule(NoFov.class).getState())
            fovModifierHand = 1;

        if (Main.INSTANCE.moduleManager.getModule(KeepSprint.class).modify) {
            fovModifierHand *= 0.9f;
            Main.INSTANCE.moduleManager.getModule(KeepSprint.class).modify = false;
        }
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderGlobal;renderEntityOutlineFramebuffer()V", shift = At.Shift.AFTER))
    private void onShader(CallbackInfo callbackInfo) {
        ShaderEvent shaderEvent = new ShaderEvent(theShaderGroup, useShader);
        TheresaClient.getInstance().getEventBus().call(shaderEvent);
        theShaderGroup = shaderEvent.getShader();
        useShader = shaderEvent.isUseShader();
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = -1), require = 1)
    private double view(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : value;
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = -1), require = 1)
    private double viewport(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && !Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? 4.0 : value;
    }

    @Inject(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void onOrientCamera(float partialTicks, CallbackInfo ci) {
        CameraEvent event = new CameraEvent
                (Minecraft.getMinecraft().getRenderViewEntity().posX, Minecraft.getMinecraft().getRenderViewEntity().posY, Minecraft.getMinecraft().getRenderViewEntity().posZ,
                        Minecraft.getMinecraft().getRenderViewEntity().prevPosX, Minecraft.getMinecraft().getRenderViewEntity().prevPosY, Minecraft.getMinecraft().getRenderViewEntity().prevPosZ,
                        Minecraft.getMinecraft().getRenderViewEntity().rotationYaw, Minecraft.getMinecraft().getRenderViewEntity().rotationPitch, Minecraft.getMinecraft().getRenderViewEntity().prevRotationYaw, Minecraft.getMinecraft().getRenderViewEntity().prevRotationPitch);

        TheresaClient.getInstance().getEventBus().call(event);
        Minecraft.getMinecraft().getRenderViewEntity().rotationYaw = event.getYaw();
        Minecraft.getMinecraft().getRenderViewEntity().rotationPitch = event.getPitch();
        Minecraft.getMinecraft().getRenderViewEntity().prevRotationYaw = event.getPrevYaw();
        Minecraft.getMinecraft().getRenderViewEntity().prevRotationPitch = event.getPrevPitch();
        Minecraft.getMinecraft().getRenderViewEntity().posX = event.getPosX();
        Minecraft.getMinecraft().getRenderViewEntity().posY = event.getPosY();
        Minecraft.getMinecraft().getRenderViewEntity().posZ = event.getPosZ();
        Minecraft.getMinecraft().getRenderViewEntity().prevPosX = event.getPrevPosX();
        Minecraft.getMinecraft().getRenderViewEntity().prevPosY = event.getPrevPosY();
        Minecraft.getMinecraft().getRenderViewEntity().prevPosZ = event.getPrevPosZ();
    }

    /**
     * @author ASOUL!
     */
    @Overwrite
    public void getMouseOver(float partialTicks) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

        if (entity != null) {
            if (Minecraft.getMinecraft().theWorld != null) {
                Minecraft.getMinecraft().mcProfiler.startSection("pick");
                Minecraft.getMinecraft().pointedEntity = null;
                double d0 = Minecraft.getMinecraft().playerController.getBlockReachDistance();
                Minecraft.getMinecraft().objectMouseOver = entity.rayTrace(d0, partialTicks);
                double d1 = d0;
                Vec3 vec3 = entity.getPositionEyes(partialTicks);
                boolean flag = false;
                int i = 3;

                if (Minecraft.getMinecraft().playerController.extendedReach()) {
                    d0 = 6.0D;
                    d1 = 6.0D;
                } else {
                    if (d0 > 3.0D) {
                        flag = true;
                    }
                }

                if (Minecraft.getMinecraft().objectMouseOver != null) {
                    d1 = Minecraft.getMinecraft().objectMouseOver.hitVec.distanceTo(vec3);
                }

                final MouseOverEvent mouseOverEvent = new MouseOverEvent(d1, flag, entity);
                TheresaClient.getInstance().getEventBus().call(mouseOverEvent);
                d0 = d1 = mouseOverEvent.getRange();
                flag = mouseOverEvent.isRangeCheck();

                Vec3 vec31 = entity.getLook(partialTicks);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                this.pointedEntity = null;
                Vec3 vec33 = null;
                float f = 1.0F;
                List<Entity> list = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply(Entity p_apply_1_) {
                        return p_apply_1_.canBeCollidedWith();
                    }
                }));
                double d2 = d1;

                for (int j = 0; j < list.size(); ++j) {
                    Entity entity1 = list.get(j);
                    float f1 = entity1.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                    if (axisalignedbb.isVecInside(vec3)) {
                        if (d2 >= 0.0D) {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1 == entity.ridingEntity && !entity.canRiderInteract()) {
                                if (d2 == 0.0D) {
                                    this.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D) {
                    this.pointedEntity = null;
                    Minecraft.getMinecraft().objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing) null, new BlockPos(vec33));
                }

                if (this.pointedEntity != null && (d2 < d1 || Minecraft.getMinecraft().objectMouseOver == null)) {
                    Minecraft.getMinecraft().objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);

                    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                        Minecraft.getMinecraft().pointedEntity = this.pointedEntity;
                    }
                }

                Minecraft.getMinecraft().mcProfiler.endSection();
            }
        }
    }


}
