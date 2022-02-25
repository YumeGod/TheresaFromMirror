package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.render.Xray;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {
    @Redirect(method = "getBlockLayer", at = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;CUTOUT_MIPPED:Lnet/minecraft/util/EnumWorldBlockLayer;"))
    private EnumWorldBlockLayer getBlockLayer() {
        return Main.INSTANCE.moduleManager.getModule(Xray.class).getState() ?
                EnumWorldBlockLayer.TRANSLUCENT : EnumWorldBlockLayer.CUTOUT_MIPPED;
    }
}
