package dev.xix.feature;

import dev.xix.gui.element.AbstractElement;

public interface ITheresaFeature {
    String getName();

    String getIdentifier();

    void addElements(final AbstractElement... elements);

    // TODO
}
