

package cn.loli.client.module;

public enum ModuleCategory {
    RENDER("Render"),
    MOVEMENT("Movement"),
    COMBAT("Combat"),
    MISC("Misc"),
    PLAYER("Player"),
    WORLD("World");

    private final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
