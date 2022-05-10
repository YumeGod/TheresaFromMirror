

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.AnimationEvent;
import cn.loli.client.module.modules.render.BlockHit;

import dev.xix.TheresaClient;
import dev.xix.event.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks);

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    /**
     * @author 65_7a
     * @reason block hitting
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        float f1 = player.getSwingProgress(partialTicks);
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
        float f4 = Main.INSTANCE.moduleManager.getModule(BlockHit.class).getState() ? f1 : 0.0F;
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(player);
        this.rotateWithPlayerRotations(player, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() instanceof net.minecraft.item.ItemMap) {
                this.renderItemMap(player, f2, f, f1);
            } else if (player.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();

                switch (enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        AnimationEvent eatAnimation = new AnimationEvent(EventType.EAT, player, partialTicks, f, f1, false);
                        TheresaClient.getInstance().getEventBus().call(eatAnimation);
                        if (eatAnimation.isCancelable()) break;
                        this.performDrinking(player, partialTicks);
                        this.transformFirstPersonItem(f, f4);
                        break;
                    case BLOCK:
                        AnimationEvent blockAnimation = new AnimationEvent(EventType.BLOCK, player, partialTicks, f, f1, false);
                        TheresaClient.getInstance().getEventBus().call(blockAnimation);
                        if (blockAnimation.isCancelable()) break;
                        this.transformFirstPersonItem(f, f4);
                        this.doBlockTransformations();
                        break;
                    case BOW:
                        AnimationEvent bowAnimation = new AnimationEvent(EventType.BOW, player, partialTicks, f, f1, false);
                        TheresaClient.getInstance().getEventBus().call(bowAnimation);
                        if (bowAnimation.isCancelable()) break;
                        this.transformFirstPersonItem(f, f4);
                        this.doBowTransformations(partialTicks, player);
                }
            } else {
                this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
            }

            this.renderItem(player, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!player.isInvisible()) {
            this.renderPlayerArm(player, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }


    @Inject(method = "transformFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void transformFirstPersonItem(float equipProgress, float swingProgress, CallbackInfo ci) {
        Main.INSTANCE.moduleManager.getModule(cn.loli.client.module.modules.render.ItemRenderer.class).transform();
    }

}
