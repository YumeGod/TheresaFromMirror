package dev.xix.property.impl;

import cn.loli.client.gui.clickui.GuiTextBox;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xix.property.AbstractTheresaProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StringProperty extends AbstractTheresaProperty<String> {
    public GuiTextBox textBox;

    public StringProperty(final String name, final String value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    public StringProperty(final String name, final String value) {
        super(name, value, () -> true);
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getPropertyValue());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                setPropertyValue(element.getAsString());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
