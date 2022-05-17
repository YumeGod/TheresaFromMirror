

package cn.loli.client.module.modules.render;

import cn.loli.client.events.Render3DEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;

public class FullBright extends Module {
    private final NumberProperty<Integer> gamma = new NumberProperty<>("Gamma", 100, 1, 100 , 1);
    private float oldGamma;

    public FullBright() {
        super("FullBright", "Increases your gamma, so you can see in the dark.", ModuleCategory.RENDER);
    }

    @Override
    protected void onEnable() {
        
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    protected void onDisable() {
        
        mc.gameSettings.gammaSetting = oldGamma;
    }

    private final IEventListener<TickEvent> onTick = e ->
    {
        if (e.getEventType() != EventType.POST) return;

        if (mc.gameSettings.gammaSetting != gamma.getPropertyValue()) {
            oldGamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = gamma.getPropertyValue();
        }
    };

}
