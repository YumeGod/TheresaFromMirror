

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.events.LoopEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.WindowResizeEvent;
import cn.loli.client.module.modules.render.BlockHit;
import cn.loli.client.utils.AnimationUtils;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
public abstract class MixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    @Mutable
    @Final
    private Session session;

    @Shadow
    long prevFrameTime;

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public abstract void displayGuiScreen(GuiScreen guiScreenIn);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void minecraftConstructor(GameConfiguration gameConfig, CallbackInfo ci) {
        new Main();
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
        Main.INSTANCE.startClient();
    }

    private long lastFrame;


    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(CallbackInfo ci) {
        final long currentTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
        final double deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;
        AnimationUtils.delta = deltaTime;

        EventManager.call(new LoopEvent());
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && currentScreen == null)
            EventManager.call(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));

        if (currentScreen == null && Keyboard.isKeyDown(Keyboard.KEY_PERIOD) && gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            displayGuiScreen(new GuiChat());
        }
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPreClientTick()V", shift = At.Shift.AFTER))
    private void onPreTick(CallbackInfo ci) {
        EventManager.call(new TickEvent(EventType.PRE));
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;onPostClientTick()V", shift = At.Shift.BEFORE))
    private void onPostTick(CallbackInfo ci) {
        EventManager.call(new TickEvent(EventType.POST));
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        Main.INSTANCE.stopClient();
    }

    @Inject(method = "resize", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;updateFramebufferSize()V", shift = At.Shift.AFTER))
    private void updateClickGuiPosition(int width, int height, CallbackInfo ci) {
        EventManager.call(new WindowResizeEvent(width, height));
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isUsingItem()Z"))
    private boolean blockHit(EntityPlayerSP player) {
        BlockHit blockHit = Main.INSTANCE.moduleManager.getModule(BlockHit.class);
        if (blockHit.getState() && !blockHit.animationsOnly.getObject()) return false;
        return player.isUsingItem();
    }
}
