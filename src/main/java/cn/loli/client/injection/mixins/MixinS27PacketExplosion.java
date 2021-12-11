package cn.loli.client.injection.mixins;


import cn.loli.client.injection.implementations.IS27PacketExplosion;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(S27PacketExplosion.class)
public class MixinS27PacketExplosion implements IS27PacketExplosion {

    @Shadow
    private float field_149152_f;

    @Shadow
    private float field_149153_g;

    @Shadow
    private float field_149159_h;

    @Override
    public void setX(float f) {
        field_149152_f = f;
    }

    @Override
    public void setY(float f) {
        field_149153_g = f;
    }

    @Override
    public void setZ(float f) {
        field_149159_h = f;
    }

}
