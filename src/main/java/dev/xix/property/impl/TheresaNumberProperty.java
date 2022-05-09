package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.util.function.Supplier;

public final class TheresaNumberProperty<T extends Number> extends AbstractTheresaProperty<T> {
    private final T minimum;
    private final T maximum;
    private final T increment;

    public TheresaNumberProperty(final String name, final T value, final T minimum, final T maximum, final T increment, final Supplier<Boolean> supplier) {
        super(name, value, supplier);

        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public TheresaNumberProperty(final String name, final T value, final T minimum, final T maximum, final T increment) {
        this(name, value, minimum, maximum, increment, () -> true);
    }
}
