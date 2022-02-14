

package cn.loli.client.module.modules.misc;

import cn.loli.client.gui.clickui.ClickGui;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventManager;
import org.lwjgl.input.Keyboard;

public class ClickGUIModule extends Module {

    public ClickGUIModule() {
        super("ClickGUI", "GUI that allows you to toggle modules and change settings", ModuleCategory.MISC, true, false, Keyboard.KEY_RSHIFT);

    }

    @Override
    protected void onEnable() {
        mc.displayGuiScreen(new ClickGui(true));
        setState(false);
        EventManager.register(this);
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
