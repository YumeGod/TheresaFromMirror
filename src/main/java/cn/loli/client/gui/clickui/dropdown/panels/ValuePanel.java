package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.module.Module;

public class ValuePanel extends Panel {
    public ValuePanel(Module module) {
        super(module.getName());
        this.update(10, 140, 110, 200);
    }
}
