

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.RenderWorldLastEvent;
import cn.loli.client.module.modules.render.ViewClip;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;dispatchRenderLast(Lnet/minecraft/client/renderer/RenderGlobal;F)V"))
    private void onRenderWorldPass(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        EventManager.call(new RenderWorldLastEvent(Minecraft.getMinecraft().renderGlobal, partialTicks));
    }

    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderHand(FI)V"))
    private void onRenderHand(CallbackInfo ci) {
        EventManager.call(new RenderEvent());
    }


    @ModifyVariable(method = { "orientCamera" },  ordinal = 3,  at = @At(value = "STORE",  ordinal = -1),  require = 1)
    private double view(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : value;
    }

    @ModifyVariable(method = { "orientCamera" },  ordinal = 7,  at = @At(value = "STORE",  ordinal = -1),  require = 1)
    private double viewport(double value) {
        return (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? Main.INSTANCE.moduleManager.getModule(ViewClip.class).dis.getObject() : (Main.INSTANCE.moduleManager.getModule(ViewClip.class).getState() && !Main.INSTANCE.moduleManager.getModule(ViewClip.class).extend.getObject()) ? 4.0 : value;
    }

}
