package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.render.NameTags;
import cn.loli.client.module.modules.render.OldAnimations;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity<T extends EntityLivingBase> {
    @Shadow
    protected List<LayerRenderer<T>> layerRenderers;

    @Shadow
    protected abstract boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures);

    @Shadow
    protected abstract void unsetBrightness();


    @Inject(method = "renderName(Lnet/minecraft/entity/EntityLivingBase;DDD)V", at = @At("HEAD"), cancellable = true)
    public void onChat(EntityLivingBase entity, double x, double y, double z, CallbackInfo ci) {
        if (Main.INSTANCE.moduleManager.getModule(NameTags.class).getState() && entity instanceof EntityPlayer)
            ci.cancel();
    }

    /**
     * @author 65_7a
     * @reason damage flash
     */
    @Overwrite
    protected void renderLayers(T entitylivingbaseIn, float f1, float f2, float partialTicks, float f3, float f4, float f5, float f6) {
        for (LayerRenderer<T> layerRenderer : layerRenderers) {
            boolean combineTextures = layerRenderer.shouldCombineTextures();

            if (Main.INSTANCE.moduleManager.getModule(OldAnimations.class).getState() && Main.INSTANCE.moduleManager.getModule(OldAnimations.class).damageFlash.getObject()) {
                combineTextures = true;
            }

            boolean brightnessSet = setBrightness(entitylivingbaseIn, partialTicks, combineTextures);
            layerRenderer.doRenderLayer(entitylivingbaseIn, f1, f2, partialTicks, f3, f4, f5, f6);

            if (brightnessSet) {
                unsetBrightness();
            }
        }
    }
}
