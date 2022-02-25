

package cn.loli.client.module.modules.world;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

public class Timer extends Module {
    private final NumberValue<Float> speed = new NumberValue<>("Speed", 2f, 0.1f, 10f);

    public Timer() {
        super("Timer", "Speeds up or slows down your game", ModuleCategory.WORLD);
    }

    @Override
    public void onDisable() {
        
        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (event.getEventType() != EventType.PRE) return;

        if (((IAccessorMinecraft) mc).getTimer().timerSpeed != speed.getObject()) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = speed.getObject();
        }
    }
}
