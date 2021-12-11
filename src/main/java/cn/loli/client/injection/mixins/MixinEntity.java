

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
public class MixinEntity {
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

    @Inject(method = "moveEntity", at = @At("HEAD"))
    private void onMove(double x, double y, double z, CallbackInfo ci) {
        if ((Object) this == Minecraft.getMinecraft().thePlayer) {
            PlayerMoveEvent event = new PlayerMoveEvent(x, y, z);
            EventManager.call(event);
        }
    }
}
