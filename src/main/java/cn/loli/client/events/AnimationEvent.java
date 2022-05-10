package cn.loli.client.events;



import dev.xix.event.Event;
import dev.xix.event.EventType;
import net.minecraft.entity.Entity;

public class AnimationEvent extends Event {

    public EventType eventType;
    public Entity entity;
    public float partialTicks;
    public float equippedProgress;
    public float SwingProgress;
    public boolean cancelable;

    public AnimationEvent(EventType eventType, Entity entity, float partialTicks, float EquippedProgress, float SwingProgress , boolean cancelable) {
        this.eventType = eventType;
        this.entity = entity;
        this.partialTicks = partialTicks;
        this.equippedProgress = EquippedProgress;
        this.SwingProgress = SwingProgress;
        this.cancelable = cancelable;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public float getEquippedProgress() {
        return this.equippedProgress;
    }

    public float getSwingProgress() {
        return this.SwingProgress;
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public void setEquippedProgress(float equippedProgress) {
        this.equippedProgress = equippedProgress;
    }

    public void setSwingProgress(float SwingProgress) {
        this.SwingProgress = SwingProgress;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }
}
