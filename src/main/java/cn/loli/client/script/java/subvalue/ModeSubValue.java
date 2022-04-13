

package cn.loli.client.script.java.subvalue;

import cn.loli.client.gui.clickui.dropdown.panels.components.ModeComponent;
import cn.loli.client.value.ModeValue;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


public class ModeSubValue extends ModeValue {
    private final String[] modes;
    public boolean open;

    public ModeSubValue(String name, String defaultVal, String... modes) {
        this(name, defaultVal, null, modes);
        this.component = new ModeComponent(this);
    }

    public ModeSubValue(String name, String defaultVal, Predicate<Integer> validator, String... modes) {
        super(name, defaultVal, validator, modes);
        this.modes = modes;
    }


    public String getCurrentMode() {
        return getModes()[getObject()];
    }

    public String[] getModes() {
        return modes;
    }


    @Override
    public boolean setObject(Integer object) {
        if (object < 0 || modes.length <= object)
            throw new IllegalArgumentException(object + " is not valid (max: " + (modes.length - 1) + ")");

        return super.setObject(object);
    }

    public boolean setObjectWithoutCallback(Integer object) {
        return super.setObjectWithoutCallback(object);
    }

    public void addToJsonObject(@NotNull JsonObject obj) {
        super.addToJsonObject(obj);
    }

    public void fromJsonObject(@NotNull JsonObject obj) {
        super.fromJsonObject(obj);
    }
}
