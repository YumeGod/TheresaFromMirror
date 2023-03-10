package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.RenderBlockEvent;

import dev.xix.TheresaClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public class MixinBlockRendererDispatcher {
    @Inject(method = "renderBlock", at = @At("HEAD"))
    public void eventUpdate(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn, CallbackInfoReturnable<Boolean> cir) {
        RenderBlockEvent event = new RenderBlockEvent(pos.getX(), pos.getY(), pos.getZ(), state.getBlock(), pos);
        Main.INSTANCE.eventBus.call(event);
    }
}
