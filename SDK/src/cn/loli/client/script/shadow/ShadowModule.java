package cn.loli.client.script.shadow;

import cn.loli.client.module.Module;

public abstract class ShadowModule extends Module {

    //Module Category
    //Combat Render Movement Player World Misc Lua
    protected ShadowModule(String name, String description, String moduleCategory) {
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
