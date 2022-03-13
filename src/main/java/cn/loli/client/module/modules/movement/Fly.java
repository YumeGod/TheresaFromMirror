

package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;

public class Fly extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "Hypixel");

    TimeHelper timer = new TimeHelper();
    private boolean jumped;
    private boolean clipped;

    public Fly() {
        super("Fly", "Reach for the skies!", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onEnable() {

        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = true;

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = true;
        }

        jumped = false;
        clipped = false;
    }

    @Override
    protected void onDisable() {

        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = false;
            mc.thePlayer.capabilities.setFlySpeed(0.05f);

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = false;
        }
    }

    @EventTarget
    private void onMotion(MotionUpdateEvent e) {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator()) {
            if (e.getEventType() == EventType.PRE) {
                if (!jumped && mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = 0.075f;
                    jumped = true;
                    return;
                }
                if (mc.thePlayer.onGround && !clipped) {
                    e.setY(e.getY() - 0.075f);
                    e.setOnGround(true);
                    clipped = true;
                }
                if (clipped) {
                    mc.thePlayer.motionY = 0.0;
                }
            }
        }
    }

    @EventTarget
    private void onMove(PlayerMoveEvent e) {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator()) {
            moveUtils.setMotion(e, 0.2);
        }
    }

}
