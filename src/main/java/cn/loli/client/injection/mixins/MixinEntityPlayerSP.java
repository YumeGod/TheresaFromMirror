

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.modules.misc.AlwaysRotate;
import cn.loli.client.module.modules.movement.NoSlowDown;
import cn.loli.client.utils.player.rotation.RotationHook;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends MixinEntity {
    @Shadow
    public float timeInPortal;
    @Shadow
    public float renderArmPitch;
    @Shadow
    public int sprintingTicksLeft;
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

        RotationHook.prevYaw = RotationHook.yaw;
        RotationHook.prevPitch = RotationHook.pitch;

        if (!Main.INSTANCE.moduleManager.getModule(AlwaysRotate.class).getState()){
            RotationHook.yaw = rotationYaw;
            RotationHook.pitch = rotationPitch;
        }

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


    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/util/AxisAlignedBB;minY:D"))
    private double posY(AxisAlignedBB instance) {
        return posY;
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
