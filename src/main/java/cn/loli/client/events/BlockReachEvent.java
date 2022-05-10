package cn.loli.client.events;

import dev.xix.event.Event;

public class BlockReachEvent extends Event {
    float range;

    public BlockReachEvent(float range) {
        this.range = range;
    }

    public float getRange() {
        return this.range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}
