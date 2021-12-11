

package cn.loli.client.gui.clickgui.components;

import cn.loli.client.gui.clickgui.AbstractComponent;
import cn.loli.client.gui.clickgui.IRenderer;
import cn.loli.client.gui.clickgui.Window;

public class Spoiler extends AbstractComponent {
    private static final int PREFERRED_HEIGHT = 28;
    public int preferredWidth;
    private String title;
    private final int preferredHeight;
    private boolean hovered;
    private ActionEventListener listener;
    private final Pane contentPane;
    private boolean opened = false;

    public Spoiler(IRenderer renderer, String title, int preferredWidth, int preferredHeight, Pane contentPane) {
        super(renderer);

        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.contentPane = contentPane;

        setTitle(title);
    }

    public Spoiler(IRenderer renderer, String title, int preferredWidth, Pane contentPane) {
        this(renderer, title, preferredWidth, PREFERRED_HEIGHT, contentPane);
    }

    @Override
    public void render() {
        if (hovered) renderer.drawRect(x, y, getWidth(), preferredHeight, Window.SECONDARY_FOREGROUND);

        renderer.drawOutline(x, y, getWidth(), preferredHeight, 1.0f, Window.SECONDARY_FOREGROUND);

        renderer.drawString(x + getWidth() / 2 - renderer.getStringWidth(title) / 2, y + renderer.getStringHeight(title) / 4, title, Window.FOREGROUND);

        if (opened) {
            updateBounds();

            contentPane.setX(getX());
            contentPane.setY(getY() + preferredHeight);

            contentPane.render();

            renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, Window.SECONDARY_FOREGROUND);
        }
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return opened && contentPane.mouseMove(x, y, offscreen);
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + preferredHeight;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if (hovered) {
                opened = !opened;

                contentPane.updateLayout();

                updateBounds();
                return true;
            }
        }

        return opened && contentPane.mousePressed(button, x, y, offscreen);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

        updateBounds();
    }

    private void updateBounds() {
        setWidth(Math.max(Math.max(renderer.getStringWidth(getTitle()), contentPane.getHeight()), preferredWidth));
        setHeight(Math.max(renderer.getStringHeight(getTitle()) * 5 / 4, preferredHeight) + (opened ? contentPane.getHeight() : 0));
    }

    public void setListener(ActionEventListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        return opened && contentPane.mouseReleased(button, x, y, offscreen);
    }

    @Override
    public boolean mouseWheel(int change) {
        return opened && contentPane.mouseWheel(change);
    }

    @Override
    public boolean keyPressed(int key, char c) {
        return opened && contentPane.keyPressed(key, c);
    }
}
