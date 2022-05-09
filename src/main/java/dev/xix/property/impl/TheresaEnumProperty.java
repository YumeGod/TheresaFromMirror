package dev.xix.property.impl;

import dev.xix.property.AbstractTheresaProperty;

import java.util.function.Supplier;

public final class TheresaEnumProperty<T extends Enum<?>> extends AbstractTheresaProperty<T> {
    private final T[] enumValues;


    public TheresaEnumProperty(final String name, final T value, final Supplier<Boolean> depedency) {
        super(name, value, depedency);
        this.enumValues = getEnumConstants();
    }

    public TheresaEnumProperty(final String name, final T value) {
        this(name, value, () -> true);
    }


    public void setValue(final int index) {
        setPropertyValue(enumValues[index]);
    }

    @SuppressWarnings("unchecked")
    private T[] getEnumConstants() {
        return (T[]) propertyValue.getClass().getEnumConstants();
    }

}
