

package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.ClickGui;
import cn.loli.client.gui.clickui.dropdown.ClickUI;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.EnumProperty;
import org.lwjgl.input.Keyboard;

public class ClickGUIModule extends Module {

    private enum MODE {
        CLASSIC("Classic"), DROPDOWN("Dropdown");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public EnumProperty mode = new EnumProperty<>("Mode", MODE.CLASSIC);

    public ClickGUIModule() {
        super("ClickGUI", "GUI that allows you to toggle modules and change settings", ModuleCategory.MISC, true, false, Keyboard.KEY_RSHIFT);

    }

    @Override
    protected void onEnable() {
        if (mode.getPropertyValue().toString().equals("Classic")) {
            mc.displayGuiScreen(new ClickGui(true));
        } else if (mode.getPropertyValue().toString().equals("Dropdown")) {
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
