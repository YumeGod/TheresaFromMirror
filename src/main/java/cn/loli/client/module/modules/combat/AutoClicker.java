

package cn.loli.client.module.modules.combat;

import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import dev.xix.event.EventType;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.security.SecureRandom;

public class AutoClicker extends Module {


    private final BooleanValue block = new BooleanValue("Ignore Facing Block", true);
    private final BooleanValue holdButton = new BooleanValue("HoldButton", true);
    private final NumberValue<Integer> minCPS = new NumberValue<>("MinCPS", 10, 1, 20);
    private final NumberValue<Integer> maxCPS = new NumberValue<>("MaxCPS", 12, 1, 20);

    private final TimeHelper timer = new TimeHelper();
    private int delay = (int) Math.floor(random(1000.0F / maxCPS.getObject(), 1000.0F / minCPS.getObject()));

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you.", ModuleCategory.COMBAT);
    }

    private double random(double min, double max) {
        return min + (max - min) * new SecureRandom().nextDouble();
    }

    @EventTarget
    public void onTick(TickEvent e) {
        if (e.getEventType() != EventType.PRE) return;

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && block.getObject()) return;

        if ((!holdButton.getObject() || Mouse.isButtonDown(0)) && mc.currentScreen == null) {
            if (maxCPS.getObject() < minCPS.getObject()) {
                maxCPS.setObject(minCPS.getObject());
            }

            if (timer.hasReached(delay)) {
                KeyBinding.setKeyBindState(-100, true);
                KeyBinding.onTick(-100);
                delay = (int) Math.floor(random(1000.0F / maxCPS.getObject(), 1000.0F / minCPS.getObject()));
                KeyBinding.setKeyBindState(-100, false);
                timer.reset();
            }
        }
    }
}
