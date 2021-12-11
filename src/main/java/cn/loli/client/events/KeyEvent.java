

package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class KeyEvent implements Event {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
