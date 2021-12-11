

package cn.loli.client.gui.clickgui.components;

import cn.loli.client.gui.clickgui.AbstractComponent;
import cn.loli.client.gui.clickgui.IRenderer;
import cn.loli.client.gui.clickgui.Window;
import cn.loli.client.gui.clickgui.layout.ILayoutManager;

import java.awt.*;

public class ScrollPane extends Pane {
    private static final double SCROLL_AMOUNT = 0.25;
    private int scrollOffset = 0;
    private boolean hovered = false;
    private int realHeight;

    public ScrollPane(IRenderer renderer, ILayoutManager layoutManager) {
        super(renderer, layoutManager);
    }

    @Override
    public void updateLayout() {
        updateLayout(getWidth(), Integer.MAX_VALUE, true);
    }

    @Override
    protected void updateLayout(int width, int height, boolean changeHeight) {
        super.updateLayout(width, height, false);

        realHeight = layout.getMaxHeight();
        validateOffset();
    }

    @Override
    public void render() {
        renderer.initMask();

        renderer.drawRect(x, y, getWidth(), getHeight(), Color.white);

        renderer.useMask();

        super.render();

        renderer.disableMask();

        int maxY = realHeight - getHeight();

        if (maxY > 0) {
            int sliderHeight = (int) (getHeight() / (double) realHeight * (double) getHeight());
            int sliderWidth = 3;

            renderer.drawRect(x + getWidth() - sliderWidth, y + (getHeight() - sliderHeight) * (scrollOffset / (double) maxY), sliderWidth, sliderHeight, Window.SECONDARY_OUTLINE);
        }
    }


    @Override
    protected void updateComponentLocation() {
        for (AbstractComponent component : components) {
            int[] ints = componentLocations.get(component);

            if (ints == null) {
                updateLayout();
                updateComponentLocation();

                return;
            }

            component.setX(x + ints[0]);
            component.setY(y + ints[1] - scrollOffset);
        }
    }


    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    @Override
    public boolean mouseWheel(int change) {
        scrollOffset -= change * SCROLL_AMOUNT;

        validateOffset();

        return super.mouseWheel(change);
    }

    private void validateOffset() {
        if (scrollOffset > realHeight - getHeight()) {
            scrollOffset = realHeight - getHeight();
        }

        if (scrollOffset < 0) {
            scrollOffset = 0;
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return super.mouseMove(x, y, offscreen || x < this.x || y < this.y || x > this.x + getWidth() || y > this.y + getHeight());
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        return super.mousePressed(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + getWidth() || y > this.y + getHeight());
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return super.mouseReleased(button, x, y, offscreen || x < this.x || y < this.y || x > this.x + getWidth() || y > this.y + getHeight());
    }

    @Override
    public void addComponent(AbstractComponent component) {
        super.addComponent(component);
    }
}
