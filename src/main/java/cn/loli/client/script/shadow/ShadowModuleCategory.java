package cn.loli.client.script.shadow;

public enum ShadowModuleCategory {
    COMBAT("Combat"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc"),
    LUA("Lua");

    private final String name;

    ShadowModuleCategory(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }
}
