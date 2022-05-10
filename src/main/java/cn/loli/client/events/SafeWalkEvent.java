package cn.loli.client.events;

import dev.xix.event.EventCancellable;

public class SafeWalkEvent extends EventCancellable {

    public boolean safe;

    public SafeWalkEvent(boolean safe) {
        this.safe = safe;
    }

    public boolean getSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }
}
