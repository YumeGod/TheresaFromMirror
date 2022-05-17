

package cn.loli.client.script.java.subvalue;


import dev.xix.property.impl.ColorProperty;

import java.awt.*;
import java.util.function.Supplier;

public class ColorSubValue extends ColorProperty {


    public ColorSubValue(String name, Color value, Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public ColorSubValue(String name, Color value) {
        super(name, value);
    }
}
