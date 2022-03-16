

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

    String getName() {
        return name;
    }

    public static ModuleCategory getCategory(String name) {
        for (ModuleCategory category : values()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
