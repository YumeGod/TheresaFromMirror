package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class TextEvent implements Event {

    String string;

    public TextEvent(String string) {
        this.string = string;
    }

    public String getText() {
        return string;
    }

    public void setText(String pass) {
        this.string = pass;
    }
}
