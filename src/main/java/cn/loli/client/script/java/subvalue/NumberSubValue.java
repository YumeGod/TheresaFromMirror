

package cn.loli.client.script.java.subvalue;

import cn.loli.client.Main;
import cn.loli.client.value.NumberValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class NumberSubValue<T extends Number> extends NumberValue<T> {
    private final T min;
    private final T max;
    public boolean clickgui_drag;

    public NumberSubValue(String name, T defaultVal, @NotNull T min, @NotNull T max) {
        this(name, defaultVal, min, max, null);
    }

    public NumberSubValue(String name, T defaultVal, @NotNull T min, @NotNull T max, @Nullable Predicate<T> validator) {
        super(name, defaultVal, min, max, validator);
        this.min = min;
        this.max = max;
        if (min.doubleValue() > max.doubleValue()) {
            Main.INSTANCE.logger.error("[NumberValue] Unexpected values: Minimum value (" + min + ") is greater than maximum value (" + max + ")");
        }
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isNumber()) {

                if (getObject() instanceof Integer) {
                    setObject((T) Integer.valueOf(obj.get(getName()).getAsNumber().intValue()));
                }
                if (getObject() instanceof Long) {
                    setObject((T) Long.valueOf(obj.get(getName()).getAsNumber().longValue()));
                }
                if (getObject() instanceof Float) {
                    setObject((T) Float.valueOf(obj.get(getName()).getAsNumber().floatValue()));
                }
                if (getObject() instanceof Double) {
                    setObject((T) Double.valueOf(obj.get(getName()).getAsNumber().doubleValue()));
                }
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
