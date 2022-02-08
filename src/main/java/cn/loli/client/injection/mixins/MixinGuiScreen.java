

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.misc.IgnoreCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
@SideOnly(Side.CLIENT)
public class MixinGuiScreen {
    @Shadow
    public Minecraft mc;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected List<GuiButton> buttonList;

    @Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true)
    private void onChat(String msg, boolean addToChat, @NotNull CallbackInfo ci) {
        if (msg.startsWith(".") && msg.length() > 1) {
            if (Main.INSTANCE.moduleManager.getModule(IgnoreCommands.class).getState())
                return;

            if (Main.INSTANCE.commandManager.executeCommand(msg)) {
                this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
            }

            ci.cancel();
        }
    }
}
