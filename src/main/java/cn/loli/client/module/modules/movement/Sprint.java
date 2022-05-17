

package cn.loli.client.module.modules.movement;


import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.EnumProperty;
import net.minecraft.potion.Potion;

public class Sprint extends Module {

    private enum MODE {
        LEGIT("Legit"), RAGE("Rage");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty mode = new EnumProperty<>("Mode", MODE.LEGIT);

    public Sprint() {
        super("Sprint", "Automatically sprints for you.", ModuleCategory.MOVEMENT);
    }

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (event.getEventType() != EventType.PRE) return;

        if (mode.getPropertyValue().toString().equals("Rage") ||
                (!mc.thePlayer.movementInput.sneak &&
                        (mc.thePlayer.movementInput.moveForward >= 0.8F) &&
                        !mc.thePlayer.isSprinting() &&
                        ((float) mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying) &&
                        !mc.thePlayer.isPotionActive(Potion.blindness))) {
            mc.thePlayer.setSprinting(true);
        }

    };


}
