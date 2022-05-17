package dev.xix.property;

import cn.loli.client.gui.clickui.components.Component;
import com.google.gson.JsonObject;

import java.util.function.Supplier;

public abstract class AbstractTheresaProperty<T> {
    protected final String name;
    protected final String identifier;
    public Component component;
    protected T propertyValue;

    protected final Supplier<Boolean> propertyDependency;

    // Animation variables
    public float clickgui_anim;


    public abstract void addToJsonObject(JsonObject obj);

    public abstract void fromJsonObject(JsonObject obj);
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

    public String getName() {
        return name;
    }

}
