

package cn.loli.client.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc"),
    LUA("Lua");

    //TODO: SkyBlocks and other mods

    private final String name;

    ModuleCategory(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return name;
    }
}
