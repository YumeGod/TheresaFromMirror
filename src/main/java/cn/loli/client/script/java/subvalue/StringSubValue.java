

package cn.loli.client.script.java.subvalue;


import dev.xix.property.impl.StringProperty;

import java.util.function.Supplier;

public class StringSubValue extends StringProperty {

    public StringSubValue(String name, String value, Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public StringSubValue(String name, String value) {
        super(name, value);
    }
}
