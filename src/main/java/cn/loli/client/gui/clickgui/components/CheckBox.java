

package cn.loli.client.gui.clickgui.components;

import cn.loli.client.gui.clickgui.AbstractComponent;
import cn.loli.client.gui.clickgui.IRenderer;
import cn.loli.client.gui.clickgui.Window;

import java.awt.*;

public class CheckBox extends AbstractComponent {
    private static final int PREFERRED_HEIGHT = 22;

    private boolean selected;
    private String title;
    private final int preferredHeight;
    private boolean hovered;
    private ValueChangeListener<Boolean> listener;

    public CheckBox(IRenderer renderer, String title, int preferredHeight) {
        super(renderer);

        this.preferredHeight = preferredHeight;

        setTitle(title);
    }

    public CheckBox(IRenderer renderer, String title) {
        this(renderer, title, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        renderer.drawRect(x, y, preferredHeight, preferredHeight, hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);

        if (selected) {
            Color color = hovered ? Window.TERTIARY_FOREGROUND : Window.SECONDARY_FOREGROUND;

            renderer.drawRect(x + 2, y + 3, preferredHeight - 5, preferredHeight - 5, new Color(color.getRed(), color.getGreen(), color.getBlue()));
        }

        renderer.drawOutline(x, y, preferredHeight, preferredHeight, 1.0f, hovered ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        renderer.drawString(x + preferredHeight + preferredHeight / 4, y + renderer.getStringHeight(title) / 4, title, Window.FOREGROUND);
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if (hovered) {

                boolean newVal = !selected;
                boolean change = true;

                if (listener != null) {
                    change = listener.onValueChange(newVal);
                }

                if (change) selected = newVal;

                return true;
            }
        }

        return false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

        setWidth(renderer.getStringWidth(title) + preferredHeight + preferredHeight / 4);
        setHeight(preferredHeight);
    }

    public void setListener(ValueChangeListener<Boolean> listener) {
        this.listener = listener;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
