package dev.xix.property;

import java.util.List;

public final class TheresaPropertyNode {
    private final String propertyNodeName;
    private final String propertyNodeIdentifier;

    private final List<AbstractTheresaProperty<?>> properties;


    public TheresaPropertyNode(final String propertyNodeName, final List<AbstractTheresaProperty<?>> properties) {
        this.propertyNodeName = propertyNodeName;
        this.propertyNodeIdentifier = propertyNodeName.replaceAll(" ", "");
        this.properties = properties;
    }

    public String getPropertyNodeName() {
        return propertyNodeName;
    }

    public String getPropertyNodeIdentifier() {
        return propertyNodeIdentifier;
    }

    public List<AbstractTheresaProperty<?>> getProperties() {
        return properties;
    }
}
