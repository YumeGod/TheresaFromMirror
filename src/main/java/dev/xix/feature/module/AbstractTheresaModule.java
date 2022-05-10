package dev.xix.feature.module;

import dev.xix.feature.ITheresaFeature;
import dev.xix.feature.module.input.IInputtableTheresaModule;
import dev.xix.feature.module.status.IToggleableTheresaModule;
import dev.xix.gui.element.AbstractElement;

import java.util.*;

public abstract class AbstractTheresaModule implements ITheresaFeature, IInputtableTheresaModule, IToggleableTheresaModule {

    protected final String name;
    protected final String identifier;

    protected final TheresaModuleCategory theresaModuleCategory;

    protected boolean enabled;
    protected int key;

    private final Map<String, AbstractElement> elements;

    protected AbstractTheresaModule(final String name, final TheresaModuleCategory category) {
        this.name = name;
        this.identifier = name.replaceAll(" ", "");
        this.theresaModuleCategory = category;

        this.elements = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void addElements(AbstractElement... elements) {
        for (final AbstractElement element : elements) {
            this.elements.put(element.getIdentifier(), element);
        }
    }

    public AbstractElement getElement(final String identifier) {
        return elements.getOrDefault(identifier, null);
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

    public Map<String, AbstractElement> getElements() {
        return elements;
    }
}
