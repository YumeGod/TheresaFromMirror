

package cn.loli.client.script.java.subvalue;


import cn.loli.client.gui.clickui.components.ModeComponent;
import dev.xix.property.impl.EnumProperty;

import java.util.function.Supplier;

public class ModeSubValue extends EnumProperty {

    public ModeSubValue(String name, Enum value, Supplier depedency) {
        super(name, value, depedency);
        this.component = new ModeComponent(this);

    }

    public ModeSubValue(String name, Enum value) {
        super(name, value);
    }
}
