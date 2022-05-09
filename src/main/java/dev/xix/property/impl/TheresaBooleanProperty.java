package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.util.function.Supplier;

public final class TheresaBooleanProperty extends AbstractTheresaProperty<Boolean> {
    public TheresaBooleanProperty(final String name, final boolean value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public TheresaBooleanProperty(final String name, final boolean value) {
        super(name, value, () -> true);
    }
}
