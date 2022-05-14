package cn.loli.client.module.modules.combat;

import cn.loli.client.events.BlockReachEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.MouseOverEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;

public class Range extends Module {

    public static final NumberProperty<Integer> range = new NumberProperty<>("Range", 6, 3, 100 , 1);

    public Range() {
        super("Range", "You get more range", ModuleCategory.COMBAT);
    }

    private final IEventListener<MouseOverEvent> onRange = e ->
    {
        e.setRange(range.getPropertyValue());
        e.setRangeCheck(false);
    };

    private final IEventListener<BlockReachEvent> onReach = e ->
    {
        e.setRange(range.getPropertyValue());
    };

}
