package cn.loli.client.events;

import dev.xix.event.EventCancellable;
import net.minecraft.util.IChatComponent;

public class ChatEvent extends EventCancellable {
    public String message;
    public boolean cancelled;
    private IChatComponent ChatComponent;

    public ChatEvent(String chat, IChatComponent ChatComponent) {
        message = chat;
        this.ChatComponent = ChatComponent;

    }

    public IChatComponent getChatComponent() {
        return this.ChatComponent;
    }

    public String getMessage() {
        return message;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public void setChatComponent(IChatComponent ChatComponent) {
        this.ChatComponent = ChatComponent;
    }
}
