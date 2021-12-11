package cn.loli.client.injection.mixins;

import cn.loli.client.injection.implementations.IS12PacketEntityVelocity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(S12PacketEntityVelocity.class)
public class MixinS12PacketEntityVelocity implements IS12PacketEntityVelocity{
    @Shadow
    private int motionX;
    @Shadow
    private int motionY;
    @Shadow
    private int motionZ;

    @Override
    public void setX(int f) {
        motionX = f;
    }

    @Override
    public void setY(int f) {
        motionY = f;
    }

    @Override
    public void setZ(int f) {
        motionZ = f;
    }

}
