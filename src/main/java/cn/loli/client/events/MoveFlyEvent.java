package cn.loli.client.events;

import dev.xix.event.Event;

public class MoveFlyEvent extends Event {
    float yaw;

    public MoveFlyEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
