package dev.xix.feature.module;

public enum TheresaModuleCategory {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    EXPLOIT("Exploit"),
    MISCELLANEOUS("Miscellaneous"),
    RENDER("Render");

    private final String name;

    TheresaModuleCategory(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
