package dev.xix.property.impl;

import cn.loli.client.gui.clickui.components.BooleanComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xix.property.AbstractTheresaProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BooleanProperty extends AbstractTheresaProperty<Boolean> {
    public BooleanProperty(final String name, final boolean value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
        this.component = new BooleanComponent(this);
    }

    public BooleanProperty(final String name, final boolean value) {
        super(name, value, () -> true);
        this.component = new BooleanComponent(this);
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getPropertyValue());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isBoolean()) {
                setPropertyValue(element.getAsBoolean());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
