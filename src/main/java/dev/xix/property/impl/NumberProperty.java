package dev.xix.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xix.property.AbstractTheresaProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class NumberProperty<T extends Number> extends AbstractTheresaProperty<T> {
    private final T minimum;
    private final T maximum;
    private final T increment;

    //Animation variables
    public boolean clickgui_drag;

    public NumberProperty(final String name, final T value, final T minimum, final T maximum, final T increment, final Supplier<Boolean> supplier) {
        super(name, value, supplier);

        this.minimum = minimum;
        this.maximum = maximum;
        this.increment = increment;
    }

    public T getIncrement() {
        return increment;
    }

    public T getMaximum() {
        return maximum;
    }

    public T getMinimum() {
        return minimum;
    }

    public NumberProperty(final String name, final T value, final T minimum, final T maximum, final T increment) {
        this(name, value, minimum, maximum, increment, () -> true);
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getPropertyValue());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {

                if (getPropertyValue() instanceof Integer) {
                    setPropertyValue((T) Integer.valueOf(obj.get(getName()).getAsNumber().intValue()));
                }
                if (getPropertyValue() instanceof Long) {
                    setPropertyValue((T) Long.valueOf(obj.get(getName()).getAsNumber().longValue()));
                }
                if (getPropertyValue() instanceof Float) {
                    setPropertyValue((T) Float.valueOf(obj.get(getName()).getAsNumber().floatValue()));
                }
                if (getPropertyValue() instanceof Double) {
                    setPropertyValue((T) Double.valueOf(obj.get(getName()).getAsNumber().doubleValue()));
                }
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
