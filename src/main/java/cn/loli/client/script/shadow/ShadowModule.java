package cn.loli.client.script.shadow;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

public abstract class ShadowModule extends Module {

    protected ShadowModule(String name, String description, ModuleCategory moduleCategory) {
        super(name, description, moduleCategory);
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        super.setState(state);
    }

    protected void onEnable() {
        super.onEnable();
    }

    protected void onDisable() {
        super.onDisable();
    }

    protected void onToggle() {
        super.onToggle();
    }
}
