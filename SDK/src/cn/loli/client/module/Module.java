

package cn.loli.client.module;

import java.util.Random;

public abstract class Module {
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final boolean canBeEnabled;
    private final boolean hidden;
    private int keybind;
    protected boolean state;


    protected Module(String name, String description, ModuleCategory moduleCategory) {
        this(name, description, moduleCategory, true, false, 0);
    }

    protected Module(String name, String description, ModuleCategory category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean getState() {
        return state;
    }


    public void setState(boolean state) {

    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onToggle() {
    }
}
