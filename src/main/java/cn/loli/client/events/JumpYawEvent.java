package cn.loli.client.events;

import dev.xix.event.EventCancellable;

public class JumpYawEvent extends EventCancellable {
    float yaw;

    public JumpYawEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }


    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

}
