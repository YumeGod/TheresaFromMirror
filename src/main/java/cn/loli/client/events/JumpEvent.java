package cn.loli.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;

public class JumpEvent extends EventCancellable {
    public float yaw;

    public JumpEvent(float yaw) {
        this.yaw = yaw;
    }
}
