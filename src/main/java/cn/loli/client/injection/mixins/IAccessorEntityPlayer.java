

package cn.loli.client.injection.mixins;

import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayer.class)
public interface IAccessorEntityPlayer {
    @Accessor
    float getSpeedInAir();

    @Accessor("speedInAir")
    void setSpeedInAir(float speedInAir);
}
