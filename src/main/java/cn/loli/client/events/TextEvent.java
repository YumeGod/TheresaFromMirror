package cn.loli.client.events;


import dev.xix.event.Event;

public class TextEvent extends Event {

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
