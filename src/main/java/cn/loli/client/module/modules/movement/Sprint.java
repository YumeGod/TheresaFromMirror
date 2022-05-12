

package cn.loli.client.module.modules.movement;

import cn.loli.client.events.JumpEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.potion.Potion;

public class Sprint extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Legit", "Legit", "Rage");

    public Sprint() {
        super("Sprint", "Automatically sprints for you.", ModuleCategory.MOVEMENT);
    }

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (event.getEventType() != EventType.PRE) return;

        if (mode.getCurrentMode().equalsIgnoreCase("Rage") ||
                (!mc.thePlayer.movementInput.sneak &&
                        (mc.thePlayer.movementInput.moveForward >= 0.8F) &&
                        !mc.thePlayer.isSprinting() &&
                        ((float) mc.thePlayer.getFoodStats().getFoodLevel() > 6.0F || mc.thePlayer.capabilities.allowFlying) &&
                        !mc.thePlayer.isPotionActive(Potion.blindness))) {
            mc.thePlayer.setSprinting(true);
        }

    };


}
