

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.MoveFlyEvent;
import cn.loli.client.events.StepEvent;
import cn.loli.client.module.modules.combat.Velocity;
import cn.loli.client.module.modules.player.SafeWalk;

import dev.xix.TheresaClient;
import dev.xix.event.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;

    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;

    @Shadow
    public boolean onGround;

    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;

    @Shadow
    public float stepHeight;

    @Shadow
    public void moveEntity(double x, double y, double z) {
    }


    @Inject(method = "moveEntity", at = @At("HEAD"))
    private void onSafe(CallbackInfo callbackInfo) {
    }


    @Redirect(method = {"moveEntity"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z", ordinal = 0))
    public boolean isOnGround(Entity entity) {
        return entity.onGround || (entity.hurtResistantTime > 5 && Main.INSTANCE.moduleManager.getModule(Velocity.class).antifall.getObject());
    }

    @Redirect(method = {"moveEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return Main.INSTANCE.moduleManager.getModule(SafeWalk.class).getState() || entity.isSneaking() ||
                (Main.INSTANCE.moduleManager.getModule(Velocity.class).getState() && (entity.hurtResistantTime > 5 && Main.INSTANCE.moduleManager.getModule(Velocity.class).antifall.getObject()));
    }

    @Redirect(method = {"moveFlying"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;rotationYaw:F"))
    public float moveFlying(Entity instance) {
        MoveFlyEvent event = new MoveFlyEvent(rotationYaw);
        TheresaClient.getInstance().getEventBus().call(event);
        return ((Object) this == Minecraft.getMinecraft().thePlayer) ? event.getYaw() : rotationYaw;
    }

    @Redirect(method = {"moveEntity"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F", ordinal = 1))
    public float onStep(Entity instance) {
        StepEvent event = new StepEvent(instance.stepHeight, EventType.PRE);
        if ((Object) this == Minecraft.getMinecraft().thePlayer)
            TheresaClient.getInstance().getEventBus().call(event);

        return event.getStepHeight();
    }

    @Inject(method = {"moveEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setEntityBoundingBox(Lnet/minecraft/util/AxisAlignedBB;)V", shift = At.Shift.AFTER, ordinal = 7))
    public void onStep(double axisalignedbb1, double axisalignedbb2, double axisalignedbb13, CallbackInfo ci) {
        StepEvent event = new StepEvent(this.stepHeight, EventType.POST);

        if ((Object) this == Minecraft.getMinecraft().thePlayer) {
            double blockHeight = event.getStepHeight() + axisalignedbb2;
            if (blockHeight % 0.015625 == 0) {
                event.setHeightStepped(blockHeight);
                TheresaClient.getInstance().getEventBus().call(event);
            }
        }

    }


}
