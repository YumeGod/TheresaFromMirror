package dev.xix.feature.impl.combat;

import cn.loli.client.events.BlockReachEvent;
import cn.loli.client.events.MouseOverEvent;
import dev.xix.feature.module.AbstractTheresaModule;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.TheresaModuleCategory;

public class Range extends AbstractTheresaModule {

    public static final NumberValue<Integer> range = new NumberValue<>("Range", 6, 3, 100);

    public Range() {
        super("Range", TheresaModuleCategory.COMBAT);
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
