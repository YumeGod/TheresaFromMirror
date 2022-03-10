package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.Entity;

public class MouseOverEvent implements Event {
    double range;
    boolean rangeCheck;
    Entity entity;

    public MouseOverEvent(final double range, final boolean rangeCheck , final Entity entity) {
        this.range = range;
        this.rangeCheck = rangeCheck;
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public double getRange() {
        return this.range;
    }

    public boolean isRangeCheck() {
        return this.rangeCheck;
    }

    public void setRange(final double range) {
        this.range = range;
    }

    public void setRangeCheck(final boolean rangeCheck) {
        this.rangeCheck = rangeCheck;
    }

    public void setEntity(final Entity entity) {
        this.entity = entity;
    }
}
