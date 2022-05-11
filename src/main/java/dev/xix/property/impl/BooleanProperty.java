package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.util.function.Supplier;

public final class BooleanProperty extends AbstractTheresaProperty<Boolean> {
    public BooleanProperty(final String name, final boolean value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public BooleanProperty(final String name, final boolean value) {
        super(name, value, () -> true);
    }
}
