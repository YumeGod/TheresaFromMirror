

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.modules.render.Scoreboard;

import dev.xix.TheresaClient;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Inject(method = "renderTooltip", at = @At("RETURN"))
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        TheresaClient.getInstance().getEventBus().call(new Render2DEvent());
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (Main.INSTANCE.moduleManager.getModule(Scoreboard.class).getState())
            callbackInfo.cancel();
    }
}
