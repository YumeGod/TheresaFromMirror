package dev.xix.feature.module;

import dev.xix.feature.ITheresaFeature;

public abstract class AbstractTheresaModule implements ITheresaFeature {
    private final String name;
    private final String identifier;

    private final TheresaModuleCategory theresaModuleCategory;

    protected AbstractTheresaModule(final String name, final TheresaModuleCategory category) {
        this.name = name;
        this.identifier = name.replaceAll(" ", "");
        this.theresaModuleCategory = category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public TheresaModuleCategory getTheresaModuleCategory() {
        return theresaModuleCategory;
    }
    
    public abstract <T extends AbstractTheresaModule> T getInstance();
}
