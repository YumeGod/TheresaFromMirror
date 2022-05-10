package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.JumpEvent;
import cn.loli.client.events.JumpYawEvent;
import cn.loli.client.module.modules.movement.NoJumpDelay;

import dev.xix.TheresaClient;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase extends MixinEntity{
    @Shadow
    private int jumpTicks;

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void headLiving(CallbackInfo callbackInfo) {
        if (Main.INSTANCE.moduleManager.getModule(NoJumpDelay.class).getState())
            jumpTicks = 0;
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void jumphandle(CallbackInfo callbackInfo) {
        JumpEvent jumpEvent = new JumpEvent();
        TheresaClient.getInstance().getEventBus().call(jumpEvent);
        if (jumpEvent.isCancelled()) callbackInfo.cancel();
    }

    @Redirect(method = "jump", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;rotationYaw:F"))
    public float onJump(EntityLivingBase instance) {
        JumpYawEvent jumpEvent = new JumpYawEvent(rotationYaw);
        TheresaClient.getInstance().getEventBus().call(jumpEvent);
        return jumpEvent.getYaw();
    }
}
