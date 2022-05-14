package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.awt.*;
import java.util.function.Supplier;

public class ColorProperty extends AbstractTheresaProperty<Color> {
    public ColorProperty(final String name, final Color value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public ColorProperty(final String name, final Color value) {
        super(name, value, () -> true);
    }
}
