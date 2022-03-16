

package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class WindowResizeEvent implements Event {
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
