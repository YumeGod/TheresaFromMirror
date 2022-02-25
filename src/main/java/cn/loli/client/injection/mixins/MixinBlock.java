

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.CollisionEvent;
import cn.loli.client.module.modules.render.Xray;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Shadow
    public abstract boolean isBlockNormalCube();

    @Inject(method = "addCollisionBoxesToList", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/block/Block;getCollisionBoundingBox(Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/AxisAlignedBB;"))
    private void onAddCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, CallbackInfo ci) {
        EventManager.call(new CollisionEvent(collidingEntity, pos.getX(), pos.getY(), pos.getZ(), mask, (Block) (Object) this));
    }

    @Redirect(method = "getBlockLayer", at = @At(value = "FIELD", target = "Lnet/minecraft/util/EnumWorldBlockLayer;SOLID:Lnet/minecraft/util/EnumWorldBlockLayer;"))
    private EnumWorldBlockLayer getBlockLayer() {
        // Client
        if (Main.INSTANCE.moduleManager.getModule(Xray.class).getState()) {
            return Main.INSTANCE.moduleManager.getModule(Xray.class).blocksToFind.contains((Object) this) ? EnumWorldBlockLayer.SOLID : EnumWorldBlockLayer.TRANSLUCENT;
        }

        return EnumWorldBlockLayer.SOLID;
    }


    /**
     * @author Loli
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public float getAmbientOcclusionLightValue() {
        // Client
        if (Main.INSTANCE.moduleManager.getModule(Xray.class).getState())
            return 1f;

        return this.isBlockNormalCube() ? 0.2F : 1.0F;
    }

}
