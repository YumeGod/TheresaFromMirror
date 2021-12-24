

package cn.loli.client.injection.mixins;

import cn.loli.client.events.PlayerMoveEvent;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    public double motionZ;

    @Shadow
    public void moveEntity(double x, double y, double z){}

    @Shadow
    public void moveFlying(float strafe, float forward, float friction){}
}
