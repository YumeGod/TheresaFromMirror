

package cn.loli.client.value;

import com.google.gson.JsonObject;

import java.util.function.Predicate;

public abstract class Value<T> {
    private final String name;
    public float clickgui_anim;
    private T object;
    private final T defaultVal;
    /**
     * The validator which is called every time the value is changed
     */
    private Predicate<T> validator;
    private IValueCallback<T> callback = null;

    protected Value(String name, T defaultVal, Predicate<T> validator) {
        this.name = name;
        this.object = defaultVal;
        this.defaultVal = defaultVal;
        this.validator = validator;
    }

    public abstract void addToJsonObject(JsonObject obj);

    public abstract void fromJsonObject(JsonObject obj);

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    public boolean setObject(T object) {
        if (validator != null && !validator.test(object)) return false;

        this.object = object;

        if (callback != null) {
            callback.onValueSet(object);
        }

        return true;
    }

    public boolean setObjectWithoutCallback(T object) {
        if (validator != null && !validator.test(object)) return false;

        this.object = object;

        return true;
    }

    public void setCallback(IValueCallback<T> callback) {
        this.callback = callback;
    }

    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    public Object getDefault() {
        return defaultVal;
    }
}
