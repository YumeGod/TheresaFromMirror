package dev.xix.property.impl;

import cn.loli.client.gui.clickui.components.ModeComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xix.property.AbstractTheresaProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public class EnumProperty<T extends Enum<?>> extends AbstractTheresaProperty<T> {
    private final T[] enumValues;

    // ClickGui variables
    public boolean open;


    public EnumProperty(final String name, final T value, final Supplier<Boolean> depedency) {
        super(name, value, depedency);
        this.enumValues = getEnumConstants();
        this.component = new ModeComponent(this);
    }

    public EnumProperty(final String name, final T value) {
        this(name, value, () -> true);
    }


    public String getCurrentMode(){
        return getPropertyValue().name();
    }

    public void setValue(final int index) {
        setPropertyValue(enumValues[index]);
    }

    @SuppressWarnings("unchecked")
    public T[] getEnumConstants() {
        return (T[]) propertyValue.getClass().getEnumConstants();
    }
    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), Arrays.binarySearch(enumValues, getPropertyValue()));
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {
                this.setValue(element.getAsInt());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
