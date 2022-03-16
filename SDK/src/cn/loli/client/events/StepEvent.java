package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

public class StepEvent implements Event {
    private float stepHeight;
    private double heightStepped;
    private final EventType eventType;

    public StepEvent(final float stepHeight , EventType eventType) {
        this.stepHeight = stepHeight;
        this.eventType = eventType;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getHeightStepped() {
        return heightStepped;
    }

    public void setHeightStepped(double heightStepped) {
        this.heightStepped = heightStepped;
    }

    public EventType getEventType() {
        return eventType;
    }

}
