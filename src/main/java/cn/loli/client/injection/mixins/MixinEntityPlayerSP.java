

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.modules.movement.NoSlowDown;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends MixinEntity {
    private double cachedX;
    private double cachedY;
    private double cachedZ;

    private float cachedRotationPitch;
    private float cachedRotationYaw;

    private float cacheStrafe = 0.0F;
    private float cacheForward = 0.0F;

    private boolean cacheGround;


    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        cachedX = posX;
        cachedY = posY;
        cachedZ = posZ;

        cachedRotationYaw = rotationYaw;
        cachedRotationPitch = rotationPitch;

        cacheGround = onGround;

        MotionUpdateEvent event = new MotionUpdateEvent(EventType.PRE, posX, posY, posZ, rotationYaw, rotationPitch, onGround);
        EventManager.call(event);

        posX = event.getX();
        posY = event.getY();
        posZ = event.getZ();

        rotationYaw = event.getYaw();
        rotationPitch = event.getPitch();
        onGround = event.isOnGround();
    }


    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerPost(CallbackInfo ci) {
        posX = cachedX;
        posY = cachedY;
        posZ = cachedZ;

        rotationYaw = cachedRotationYaw;
        rotationPitch = cachedRotationPitch;

        onGround = cacheGround;

        EventManager.call(new MotionUpdateEvent(EventType.POST, posX, posY, posZ, rotationYaw, rotationPitch, onGround));
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void onUpdate(CallbackInfo ci) {
        EventManager.call(new UpdateEvent());
    }


    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    private void onNoSlowEnable(CallbackInfo callbackInfo) {
        if (this.isSlow()) {
            return;
        }

        if (Main.INSTANCE.moduleManager.getModule(NoSlowDown.class).getState()) {
            this.cacheStrafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
            this.cacheForward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;pushOutOfBlocks(DDD)Z", shift = At.Shift.BEFORE))
    public void onToggledTimerZero(CallbackInfo callbackInfo) {
        if (this.isSlow()) {
            return;
        }

        if (Main.INSTANCE.moduleManager.getModule(NoSlowDown.class).getState()) {
            Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe = this.cacheStrafe;
            Minecraft.getMinecraft().thePlayer.movementInput.moveForward = this.cacheForward;
        }
    }


    public final boolean isSlow() {
        return !Minecraft.getMinecraft().thePlayer.isUsingItem() || Minecraft.getMinecraft().thePlayer.isRiding();
    }


    @Override
    public void moveEntity(double x, double y, double z) {
        PlayerMoveEvent moveEvent = new PlayerMoveEvent(x, y, z);
        EventManager.call(moveEvent);

        if (moveEvent.isCancelled())
            return;

        x = moveEvent.getX();
        y = moveEvent.getY();
        z = moveEvent.getZ();

        super.moveEntity(x, y, z);
    }


}
