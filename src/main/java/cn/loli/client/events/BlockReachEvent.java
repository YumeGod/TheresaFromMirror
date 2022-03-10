package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class BlockReachEvent implements Event {
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
