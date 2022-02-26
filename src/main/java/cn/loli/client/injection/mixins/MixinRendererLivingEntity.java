package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.render.ESP;
import cn.loli.client.module.modules.render.OldAnimations;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
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
        if (Main.INSTANCE.moduleManager.getModule(ESP.class).getState() && entity instanceof EntityPlayer)
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

    @Redirect(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    protected void renderModel(ModelBase instance, Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        ESP esp = Main.INSTANCE.moduleManager.getModule(ESP.class);
        Color color = esp.chamsColor.getObject();
        Color color2 = esp.throughWallsColor.getObject();

        if (esp.getState() && esp.chams.getObject() && !entityIn.isInvisible()) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glDisable(GL11.GL_LIGHTING);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
            GL11.glColor4d(color2.getRed() / 255D, color2.getGreen() / 255D, color2.getBlue() / 255D, 1);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            instance.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glColor4d(color.getRed() / 255D, color.getGreen() / 255D, color.getBlue() / 255D, 1);
            instance.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
            GL11.glColor4d(1, 1, 1, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        } else {
            instance.render(entityIn, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale);
        }

    }
}
