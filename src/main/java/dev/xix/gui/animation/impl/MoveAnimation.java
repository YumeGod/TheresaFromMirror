package dev.xix.gui.animation.impl;

import dev.xix.gui.animation.AbstractAnimation;
import dev.xix.gui.animation.AnimationEasing;

public final class MoveAnimation extends AbstractAnimation {
    private final double x;
    private final double y;

    public MoveAnimation(final double x, final double y, final long duration, final AnimationEasing easing) {
        super(duration, easing);
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean process() {
        if (element.isAtPosition(x, y)) return true;

        element.setPosition((float) (startElement.getPosX() + (x - startElement.getPosX()) * getCompletion()), (float) (startElement.getPosY() + (y - startElement.getPosY()) * getCompletion()));

        return false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
