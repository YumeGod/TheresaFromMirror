package cn.loli.client.injection.mixins;


import cn.loli.client.events.TextEvent;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String renderString(final String string) {
        final TextEvent textEvent = new TextEvent(string);
        EventManager.call(textEvent);
        return textEvent.getText();
    }

    @ModifyVariable(method = "getStringWidth", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private String getStringWidth(final String string) {
        final TextEvent textEvent = new TextEvent(string);
        EventManager.call(textEvent);
        return textEvent.getText();
    }

}