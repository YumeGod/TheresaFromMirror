

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.misc.IgnoreCommands;
import net.minecraft.client.gui.GuiChat;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiChat.class)

public abstract class MixinGuiChat {
    @Shadow
    private boolean waitingOnAutocomplete;

    @Shadow
    public abstract void onAutocompleteResponse(String[] p_146406_1_);

    @Inject(method = "sendAutocompleteRequest", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void autocomplete(String cmd, String p_146405_2_, @NotNull CallbackInfo ci) {
        if (cmd.startsWith(".")) {
            String[] ls = Main.INSTANCE.commandManager.autoComplete(cmd).toArray(new String[0]);

            if (ls.length == 0 || cmd.toLowerCase().endsWith(ls[ls.length - 1].toLowerCase()))
                return;

            waitingOnAutocomplete = true;
            onAutocompleteResponse(ls);
            ci.cancel();
        }
    }
}
