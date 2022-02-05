

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.SafeWalkEvent;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.module.modules.combat.Velocity;
import cn.loli.client.module.modules.movement.NoSlowDown;
import cn.loli.client.module.modules.player.SafeWalk;
import cn.loli.client.utils.ChatUtils;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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

    SafeWalkEvent event;

    @Shadow
    public double motionX;
    @Shadow
    public double motionZ;

    @Shadow
    public void moveEntity(double x, double y, double z) {
    }

    @Shadow
    public void moveFlying(float strafe, float forward, float friction) {
    }

    @Inject(method = "moveEntity", at = @At("HEAD"))
    private void onSafe(CallbackInfo callbackInfo) {
    }


    @Redirect(method = {"moveEntity"}, at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;onGround:Z", ordinal = 0))
    public boolean isOnGround(Entity entity) {
        return entity.onGround ||
                (entity.hurtResistantTime > 0 && Main.INSTANCE.moduleManager.getModule(Velocity.class).getState() && Main.INSTANCE.moduleManager.getModule(Velocity.class).antifall.getObject());
    }

    @Redirect(method = {"moveEntity"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
    public boolean isSneaking(Entity entity) {
        return Main.INSTANCE.moduleManager.getModule(SafeWalk.class).getState() || entity.isSneaking() || (entity.hurtResistantTime > 0 &&
                Main.INSTANCE.moduleManager.getModule(Velocity.class).getState() && Main.INSTANCE.moduleManager.getModule(Velocity.class).antifall.getObject());
    }
}
