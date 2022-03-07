

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.Render3DEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.RenderWorldLastEvent;
import cn.loli.client.events.RotationEvent;
import cn.loli.client.module.modules.combat.KeepSprint;
import cn.loli.client.module.modules.render.NoFov;
import cn.loli.client.module.modules.render.ViewClip;
import cn.loli.client.utils.player.rotation.RotationHook;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private float fovModifierHand;

    @Final
    @Shadow
    private int[] lightmapColors;

    @Final
    @Shadow
    private DynamicTexture lightmapTexture;

    @Shadow
    private boolean lightmapUpdateNeeded;

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V"))
    private void onRenderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        EventManager.call(new RenderWorldLastEvent(Minecraft.getMinecraft().renderGlobal, partialTicks));
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V"))
    private void onRenderHand(CallbackInfo ci) {
        EventManager.call(new RenderEvent());
    }

    @Inject(method = "renderWorldPass", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;disableFog()V", shift = At.Shift.AFTER))
    private void eventRender3D(int pass, float partialTicks, long finishTimeNano, CallbackInfo callbackInfo) {
        Render3DEvent eventRender = new Render3DEvent(pass, partialTicks, finishTimeNano);
        EventManager.call(eventRender);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "updateFovModifierHand", at = @At("RETURN"))
    private void updateFovModifierHand(CallbackInfo callbackInfo) {
        if (Main.INSTANCE.moduleManager.getModule(NoFov.class).getState())
            fovModifierHand = 1;

        if (Main.INSTANCE.moduleManager.getModule(KeepSprint.class).modify) {
            fovModifierHand *= 0.9f;
            Main.INSTANCE.moduleManager.getModule(KeepSprint.class).modify = false;
        }
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;smoothCamera:Z", shift = At.Shift.BEFORE))
    private void onRotationHook(CallbackInfo callbackInfo) {
   /*     RotationHook.prevYaw = RotationHook.yaw;
        RotationHook.prevPitch = RotationHook.pitch;
        final RotationEvent rotationEvent = new RotationEvent(Minecraft.getMinecraft().thePlayer.rotationYaw, Minecraft.getMinecraft().thePlayer.rotationPitch);
        EventManager.call(rotationEvent);
        RotationHook.yaw = rotationEvent.getYaw();
        RotationHook.pitch = rotationEvent.getPitch();

    */
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = -1), require = 1)
    private double view(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : value;
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = -1), require = 1)
    private double viewport(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && !Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? 4.0 : value;
    }


}
