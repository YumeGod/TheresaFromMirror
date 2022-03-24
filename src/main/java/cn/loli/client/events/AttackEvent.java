package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.Entity;

public class AttackEvent implements Event {
    Entity entity;

    public AttackEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
