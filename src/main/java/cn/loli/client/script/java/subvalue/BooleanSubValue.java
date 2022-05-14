

package cn.loli.client.script.java.subvalue;


import dev.xix.property.impl.BooleanProperty;

import java.util.function.Supplier;

public class BooleanSubValue extends BooleanProperty {

    public BooleanSubValue(String name, boolean value, Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public BooleanSubValue(String name, boolean value) {
        super(name, value);
    }
}
