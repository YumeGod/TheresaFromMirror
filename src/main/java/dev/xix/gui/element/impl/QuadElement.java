package dev.xix.gui.element.impl;

import dev.xix.gui.element.AbstractElement;

public final class QuadElement extends AbstractElement {
    private int color;

    public QuadElement(String identifier, float x, float y, float width, float height, int color) {
        super(identifier, x, y, width, height);
        this.color = color;
        this.isClickable = false;
    }

    public AbstractElement setColor(final int color) {
        this.color = color;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public void renderElement() {
        // RenderingUtil.drawRect(renderPosX, renderPosY, width, height, color);
    }
}
