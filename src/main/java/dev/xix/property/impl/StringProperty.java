package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.util.function.Supplier;

public class StringProperty extends AbstractTheresaProperty<String> {
    public StringProperty(final String name, final String value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public StringProperty(final String name, final String value) {
        super(name, value, () -> true);
    }
}
