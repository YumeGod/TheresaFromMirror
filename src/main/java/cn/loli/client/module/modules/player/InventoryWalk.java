package cn.loli.client.module.modules.player;

import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InventoryWalk extends Module {

    private final KeyBinding[] keyBindings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindJump};;

    public InventoryWalk() {
        super("InventoryWalk", "Allows you to walk while your inventory is open", ModuleCategory.PLAYER);
    }

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiEditSign)) {
            for (KeyBinding keyBinding : this.keyBindings) {
                ((IAccessorKeyBinding) keyBinding).setPressed(Keyboard.isKeyDown(keyBinding.getKeyCode()));
            }
        }
    };

}
