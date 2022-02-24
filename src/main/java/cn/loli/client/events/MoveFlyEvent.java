package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class MoveFlyEvent implements Event {
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
