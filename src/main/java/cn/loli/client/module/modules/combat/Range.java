package cn.loli.client.module.modules.combat;

import cn.loli.client.events.BlockReachEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.MouseOverEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;

import dev.xix.event.bus.IEventListener;

public class Range extends Module {

    public static final NumberValue<Integer> range = new NumberValue<>("Range", 6, 3, 100);

    public Range() {
        super("Range", "You get more range", ModuleCategory.COMBAT);
    }

    private final IEventListener<MouseOverEvent> onRange = e ->
    {
        e.setRange(range.getObject());
        e.setRangeCheck(false);
    };

    private final IEventListener<BlockReachEvent> onReach = e ->
    {
        e.setRange(range.getObject());
    };

}
