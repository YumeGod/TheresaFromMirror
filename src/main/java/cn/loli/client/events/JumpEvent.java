package cn.loli.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class JumpEvent extends EventCancellable {
    float yaw;

    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }


    public void setYaw(float yaw) {
        this.yaw = yaw;
    }


}
