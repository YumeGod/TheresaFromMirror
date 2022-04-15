

package cn.loli.client.value;

import cn.loli.client.gui.clickui.dropdown.panels.components.Component;
import cn.loli.client.gui.clickui.dropdown.panels.components.ModeComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Predicate;

public class ModeValue extends Value<Integer> {
    private final String[] modes;
    public boolean open;

    public ModeValue(String name, String defaultVal, String... modes) {

        this(name, defaultVal, null, modes);
    }

    public ModeValue(String name, String defaultVal, Predicate<Integer> validator, String... modes) {
        super(name, 0, validator);
        if(!Arrays.asList(modes).contains(defaultVal)) {
            modes[modes.length - 1] = defaultVal;
        }
        this.modes = modes;
        this.component = new ModeComponent(this);

        setObject(defaultVal);
    }

    public String getCurrentMode() {
        return getModes()[getObject()];
    }

    public String[] getModes() {
        return modes;
    }

    private void setObject(String s) {
        int object = -1;

        for (int i = 0; i < modes.length; i++) {
            String mode = modes[i];

            if (mode.equalsIgnoreCase(s)) object = i;
        }

        if (object == -1) throw new IllegalArgumentException("Value '" + object + "' wasn't found");

        setObjectWithoutCallback(object);
    }

    @Override
    public boolean setObject(Integer object) {
        if (object < 0 || modes.length <= object)
            throw new IllegalArgumentException(object + " is not valid (max: " + (modes.length - 1) + ")");

        return super.setObject(object);
    }

    @Override
    public boolean setObjectWithoutCallback(Integer object) {
        if (object < 0 || modes.length <= object)
            throw new IllegalArgumentException(object + " is not valid (max: " + (modes.length - 1) + ")");

        return super.setObjectWithoutCallback(object);
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
                setObject(element.getAsInt());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
