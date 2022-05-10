package cn.loli.client.events;


import dev.xix.event.Event;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event {
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
