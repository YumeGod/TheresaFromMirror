

package cn.loli.client.module.modules.render;

import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import dev.xix.event.EventType;

public class FullBright extends Module {
    private final NumberValue<Integer> gamma = new NumberValue<>("Gamma", 100, 1, 100);
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

    @EventTarget
    public void onTick(TickEvent e) {
        if (e.getEventType() != EventType.POST) return;

        if (mc.gameSettings.gammaSetting != gamma.getObject()) {
            oldGamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = gamma.getObject();
        }
    }
}
