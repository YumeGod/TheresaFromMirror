

package cn.loli.client.module.modules.movement;

import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.MoveUtils;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;

public class Fly extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "Hypixel");

    TimeHelper timer = new TimeHelper();


    public Fly() {
        super("Fly", "Reach for the skies!", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = true;

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = true;
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = false;
            mc.thePlayer.capabilities.setFlySpeed(0.05f);

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = false;
        }
    }


    @EventTarget
    private void onMove(PlayerMoveEvent e) {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator()) {
            e.setX((mc.thePlayer.motionX = 0));
            e.setY((mc.thePlayer.motionY = 0));
            e.setZ((mc.thePlayer.motionZ = 0));

            if (MoveUtils.isOnGround(0.01)){
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.004 * Math.random(), mc.thePlayer.posZ);
                timer.reset(); // Don't forget reset
            }

            if (PlayerUtils.isMoving2()) {
                if (timer.hasReached(1200)) {
                    double playerYaw = Math.toRadians(mc.thePlayer.rotationYaw);
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 5 * -Math.sin(playerYaw), mc.thePlayer.posY - 2, mc.thePlayer.posZ + 5 * Math.cos(playerYaw));
                    timer.reset(); // Don't forget reset
                }
            } else {
                MoveUtils.setMotion(e, 0);
            }
        }
    }

}
