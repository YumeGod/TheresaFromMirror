package dev.xix.gui.element;

import dev.xix.gui.animation.AbstractAnimation;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractElement implements Cloneable {

    protected final Map<String, AbstractElement> elements;
    protected final String identifier;

    protected HorizontalAlignment horizontalAlignment;
    protected VerticalAlignment verticalAlignment;

    protected boolean visible;
    protected AbstractElement parent;

    // 绝对位置
    protected double renderPosX;
    protected double renderPosY;

    // 相对位置
    protected double posX;
    protected double posY;

    protected double width;
    protected double height;


    protected float rotation;
    protected double scale;

    protected boolean isClickable;

    protected AbstractAnimation animation;

    public AbstractElement(final String identifier, final double x, final double y, final double width, final double height) {
        this.identifier = identifier;

        this.posX = x;
        this.posY = y;

        this.height = height;
        this.width = width;

        this.rotation = 0;
        this.scale = 1;

        this.visible = true;
        this.elements = new HashMap<>();
    }

    public AbstractElement(final String identifier, final double x, final double y) {
        this(identifier, x, y, 0, 0);
    }

    public AbstractElement(final String identifier) {
        this(identifier, 0, 0);
    }

    public void handleElementRender() {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(renderPosX, renderPosY, 0);
        GlStateManager.rotate(rotation, 1F, 1F, 1F);

        this.renderElement();

        if (animation != null) {
            if (animation.process()) animation = null;
        }

        for (final AbstractElement elementChild : elements.values()) {
            elementChild.renderElement();
        }

        GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
        GlStateManager.translate(-renderPosX, -renderPosY, 0);
        GlStateManager.rotate(-rotation, 1F, 1F, 1F);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
    }

    public AbstractElement setAnimation(final AbstractAnimation animation) {
        if (animation != null) {
            // TODO
        }
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Map<String, AbstractElement> getElements() {
        return elements;
    }

    public final boolean isAtPosition(final double x, final double y) {
        return (this.posX == x && this.posY == y);
    }

    public final AbstractElement setPosition(final float x, final float y) {
        if (this.posX != x || this.posY != y) {
            this.posX = x;
            this.posY = y;
            updatePosition();
        }
        return this;
    }

    public final AbstractElement setParent(final AbstractElement element) {
        parent.elements.remove(this.getIdentifier());
        element.addElement(this);
        parent = element;
        return this;
    }

    public AbstractElement addElement(final AbstractElement element) {
        if (elements.containsKey(element.getIdentifier())) return elements.get(element.getIdentifier());

        element.parent = this;
        element.updatePosition();

        elements.put(element.getIdentifier(), element);
        return element;
    }

    public final AbstractElement addElements(final AbstractElement... elements) {
        Arrays.stream(elements).forEach(this::addElement);
        return this;
    }

    public AbstractElement getParent() {
        return this.parent;
    }

    public void updatePosition() {
        float parentWidth;
        float parentHeight;

        if (parent != null) {
            this.renderPosX = parent.renderPosX;
            this.renderPosY = parent.renderPosY;
            parentWidth = (float) parent.width;
            parentHeight = (float) parent.height;
        } else {
            this.renderPosX = 0;
            this.renderPosY = 0;
            parentWidth = (float) Display.getWidth() / 2F;
            parentHeight = (float) Display.getHeight() / 2F;
        }

        switch (horizontalAlignment) {
            case RIGHT:
                this.renderPosX += parentWidth - posX - this.width;
                break;
            case CENTER:
                this.renderPosX += (parentWidth / 2) + posX - (this.width / 2);
                break;
            case LEFT:
                this.renderPosX += posX;
                break;
        }

        switch (verticalAlignment) {
            case TOP:
                this.renderPosY += posY;
                break;
            case CENTER:
                this.renderPosY += (parentHeight / 2) + posY - (this.height / 2);
                break;
            case BOTTOM:
                this.renderPosY += parentHeight - posY - this.height;
                break;
        }

        for (final AbstractElement child : elements.values()) {
            child.updatePosition();
        }
    }


    public double getRenderPosX() {
        return renderPosX;
    }

    public void setRenderPosX(double renderPosX) {
        this.renderPosX = renderPosX;
    }

    public double getRenderPosY() {
        return renderPosY;
    }

    public void setRenderPosY(double renderPosY) {
        this.renderPosY = renderPosY;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public AbstractAnimation getAnimation() {
        return animation;
    }

    protected abstract void renderElement();

    @Override
    public AbstractElement clone() {
        try {
            return (AbstractElement) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public AbstractElement setHAlignment(final HorizontalAlignment alignment) {
        if (alignment != this.horizontalAlignment) {
            this.horizontalAlignment = alignment;
            updatePosition();
        }
        return this;
    }

    public AbstractElement setVAlignment(final VerticalAlignment alignment) {
        if (alignment != this.verticalAlignment) {
            this.verticalAlignment = alignment;
            updatePosition();
        }
        return this;
    }

    public AbstractElement setWidth(final float width) {
        return setSize(width, this.height);
    }

    public AbstractElement setHeight(float height) {
        return setSize(this.width, height);
    }

    public AbstractElement setSize(double width, double height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            updatePosition();
        }
        return this;
    }

    public AbstractElement setRotation(final float rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            updatePosition();
        }
        return this;
    }

    public AbstractElement setScale(final float scale) {
        if (this.scale != scale) {
            this.scale = scale;
            updatePosition();
        }
        return this;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public double getScale() {
        return scale;
    }

    public float getRotation() {
        return rotation;
    }

}
