

package cn.loli.client.injection.mixins;

import cn.loli.client.events.Render2DEvent;
import com.darkmagician6.eventapi.EventManager;
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
        EventManager.call(new Render2DEvent());
    }
}
