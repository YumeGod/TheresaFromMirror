

package cn.loli.client.module.modules.combat;

import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.security.SecureRandom;

public class AutoClicker extends Module {


    private final BooleanProperty block = new BooleanProperty("Ignore Facing Block", true);
    private final BooleanProperty holdButton = new BooleanProperty("HoldButton", true);
    private final NumberProperty<Integer> minCPS = new NumberProperty<>("MinCPS", 10, 1, 20 , 1);
    private final NumberProperty<Integer> maxCPS = new NumberProperty<>("MaxCPS", 12, 1, 20 , 1);

    private final TimeHelper timer = new TimeHelper();
    private int delay = (int) Math.floor(random(1000.0F / maxCPS.getPropertyValue(), 1000.0F / minCPS.getPropertyValue()));

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you.", ModuleCategory.COMBAT);
    }

    private double random(double min, double max) {
        return min + (max - min) * new SecureRandom().nextDouble();
    }

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (event.getEventType() != EventType.PRE) return;

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && block.getPropertyValue()) return;

        if ((!holdButton.getPropertyValue() || Mouse.isButtonDown(0)) && mc.currentScreen == null) {
            if (maxCPS.getPropertyValue() < minCPS.getPropertyValue()) {
                maxCPS.setPropertyValue(minCPS.getPropertyValue());
            }

            if (timer.hasReached(delay)) {
                KeyBinding.setKeyBindState(-100, true);
                KeyBinding.onTick(-100);
                delay = (int) Math.floor(random(1000.0F / maxCPS.getPropertyValue(), 1000.0F / minCPS.getPropertyValue()));
                KeyBinding.setKeyBindState(-100, false);
                timer.reset();
            }
        }
    };

}
