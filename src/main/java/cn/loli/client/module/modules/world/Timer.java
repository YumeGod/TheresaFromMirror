

package cn.loli.client.module.modules.world;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;

public class Timer extends Module {
    private final NumberProperty<Float> speed = new NumberProperty<>("Speed", 2f, 0.1f, 10f, 0.01f);

    public Timer() {
        super("Timer", "Speeds up or slows down your game", ModuleCategory.WORLD);
    }

    @Override
    public void onDisable() {

        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
    }

    private final IEventListener<TickEvent> onTick = event -> {
        if (event.getEventType() != EventType.PRE) return;

        if (((IAccessorMinecraft) mc).getTimer().timerSpeed != speed.getPropertyValue()) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = speed.getPropertyValue();
        }
    };


}
