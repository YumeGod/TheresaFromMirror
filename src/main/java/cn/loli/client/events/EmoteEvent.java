package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.model.ModelBiped;

public class EmoteEvent implements Event {
    public ModelBiped biped;
    public EventType type;

    public EmoteEvent(ModelBiped biped , EventType type) {
        this.biped = biped;
        this.type = type;
    }

    public ModelBiped getBiped() {
        return this.biped;
    }


    public EventType getEventType() {
        return type;
    }

}
