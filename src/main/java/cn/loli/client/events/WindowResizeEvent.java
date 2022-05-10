package cn.loli.client.events;

import dev.xix.event.Event;

public class WindowResizeEvent extends Event {
    private final int width;
    private final int height;

    public WindowResizeEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
