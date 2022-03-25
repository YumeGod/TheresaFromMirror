package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class EmoteEvent implements Event {
    public ModelBiped biped;
    public EventType type;
    public Entity entity;

    public EmoteEvent(ModelBiped biped , Entity entity, EventType type) {
        this.biped = biped;
        this.type = type;
        this.entity = entity;
    }

    public ModelBiped getBiped() {
        return this.biped;
    }


    public EventType getEventType() {
        return type;
    }

}
