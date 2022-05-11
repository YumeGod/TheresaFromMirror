

package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.ClickGui;
import cn.loli.client.gui.clickui.dropdown.ClickUI;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;

import org.lwjgl.input.Keyboard;

public class ClickGUIModule extends Module {

    public ModeValue mode = new ModeValue("Mode", "Classic", "Classic","Dropdown");

    public ClickGUIModule() {
        super("ClickGUI", "GUI that allows you to toggle modules and change settings", ModuleCategory.MISC, true, false, Keyboard.KEY_RSHIFT);

    }

    @Override
    protected void onEnable() {
        if(mode.getCurrentMode().equals("Classic")) {
            mc.displayGuiScreen(new ClickGui(true));
        }else if (mode.getCurrentMode().equals("Dropdown")) {
            mc.displayGuiScreen(new ClickUI());
        }
        setState(false);
         Main.INSTANCE.eventBus.register(this);
    }

    @Override
    public void setState(boolean state) {
        if (state) {
            this.state = true;
            onEnable();
        } else {
            this.state = false;
            onDisable();
        }
    }

}
