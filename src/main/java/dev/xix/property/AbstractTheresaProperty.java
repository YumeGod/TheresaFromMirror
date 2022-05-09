package dev.xix.property;

import java.util.function.Supplier;

public abstract class AbstractTheresaProperty<T> {
    protected final String name;
    protected final String identifier;

    protected T propertyValue;

    protected final Supplier<Boolean> propertyDependency;


    protected AbstractTheresaProperty() {
        this(null, null);
    }

    protected AbstractTheresaProperty(final String name, final T propertyValue) {
        this(name, propertyValue, () -> true);
    }

    protected AbstractTheresaProperty(final String name, final T propertyValue, final Supplier<Boolean> propertyDependency) {
        this.name = name;
        this.identifier = name.replaceAll(" ", "");
        this.propertyValue = propertyValue;
        this.propertyDependency = propertyDependency;
    }

    public T getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(final T value) {
        this.propertyValue = value;
    }

}
