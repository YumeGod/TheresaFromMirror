package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.EnumWorldBlockLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockGrass.class)
public class MixinBlockGrass {

}
