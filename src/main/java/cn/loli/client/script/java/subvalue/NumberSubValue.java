

package cn.loli.client.script.java.subvalue;


import cn.loli.client.gui.clickui.dropdown.panels.components.ModeComponent;
import cn.loli.client.gui.clickui.dropdown.panels.components.NumberComponent;
import dev.xix.property.impl.NumberProperty;

import java.util.function.Supplier;

public class NumberSubValue<T extends Number> extends NumberProperty<T> {

    public NumberSubValue(String name, T value, T minimum, T maximum, T increment, Supplier<Boolean> supplier) {
        super(name, value, minimum, maximum, increment, supplier);
        this.component = new NumberComponent(this);

    }

    public NumberSubValue(String name, T value, T minimum, T maximum, T increment) {
        super(name, value, minimum, maximum, increment);
    }
}
