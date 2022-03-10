package cn.loli.client.module.modules.combat;

import cn.loli.client.events.BlockReachEvent;
import cn.loli.client.events.MouseOverEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;

public class Range extends Module {

    public static final NumberValue<Integer> range = new NumberValue<>("Range", 6, 3, 100);

    public Range() {
        super("Range", "You get more range", ModuleCategory.COMBAT);
    }

    @EventTarget
    public void onRange(MouseOverEvent event) {
        event.setRange(range.getObject());
        event.setRangeCheck(false);
    }

    @EventTarget
    public void onRange(BlockReachEvent event) {
        event.setRange(range.getObject());
    }
}
