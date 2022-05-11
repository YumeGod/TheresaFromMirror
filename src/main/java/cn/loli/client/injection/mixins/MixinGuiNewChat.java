package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.ChatEvent;

import dev.xix.TheresaClient;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNewChat.class)
public class MixinGuiNewChat {

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"), cancellable = true)
    private void eventchat(IChatComponent p_146234_1_, int p_146234_2_, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(p_146234_1_.getUnformattedText() , p_146234_1_);
        Main.INSTANCE.eventBus.call(event);

        if (event.cancelled) ci.cancel();
    }
}
