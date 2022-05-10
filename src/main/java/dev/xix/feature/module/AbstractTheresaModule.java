package dev.xix.feature.module;

import dev.xix.feature.ITheresaFeature;
import dev.xix.feature.module.input.IInputtableTheresaModule;
import dev.xix.feature.module.status.IToggleableTheresaModule;

public abstract class AbstractTheresaModule implements ITheresaFeature, IInputtableTheresaModule, IToggleableTheresaModule {
    protected final String name;
    protected final String identifier;

    protected final TheresaModuleCategory theresaModuleCategory;

    protected boolean enabled;
    protected int key;

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

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(final int key) {
        this.key = key;
    }

    @Override
    public void pressKey() {
        toggle();
    }

    @Override
    public boolean getEnabled() {
        return false;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }
}
