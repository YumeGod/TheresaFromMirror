package cn.loli.client.module.modules.movement;

import cn.loli.client.Main;
import cn.loli.client.events.JumpEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.combat.Aura;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.util.MathHelper;

import java.util.concurrent.ThreadLocalRandom;

public class Speed extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Mini", "PacketAbusing", "Mini");
    private final NumberValue<Float> multiply = new NumberValue<>("Multiply", 1f, 1f, 2f);
    private final BooleanValue boost = new BooleanValue("Boost", true);
    private final BooleanValue clips = new BooleanValue("Clips", true);

    double distance;
    int stage;
    double speed, less;
    boolean lessSlow;

    public Speed() {
        super("Speed", "Just You Speed Boost", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

        if (mc.thePlayer != null && mc.theWorld != null) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.02F);
            stage = 0;
            speed = 0;
            less = 0;
            lessSlow = false;
            distance = 0;
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if ("PacketAbusing".equals(modes.getCurrentMode())) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            if (mc.thePlayer.onGround) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 15000.0F;
                } else {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                }
            }
        }
    }

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        switch (modes.getCurrentMode()) {
            case "Mini": {
                if (playerUtils.isMoving2()) {
                    if (playerUtils.isInLiquid()) return;
                    if (boost.getObject()) {
                        speed = getHypixelSpeed(stage) * 0.96;
                        if (speed < moveUtils.getBaseMoveSpeed())
                            speed = moveUtils.getBaseMoveSpeed();
                    } else {
                        speed = moveUtils.getBaseMoveSpeed();
                    }

                    moveUtils.setMotion(event, speed);
                    stage++;
                }
                break;
            }
        }

    }

    @EventTarget
    private void onMove(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            switch (modes.getCurrentMode()) {
                case "Mini": {
                    if (playerUtils.isMoving2()) {
                        if (playerUtils.isInLiquid())
                            return;

                        double motionX = mc.gameSettings.keyBindForward.isKeyDown() && clips.getObject() ? (MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;
                        double motionZ = mc.gameSettings.keyBindForward.isKeyDown() && clips.getObject() ? (MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;

                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX - motionX, mc.thePlayer.posY + (0.11D * multiply.getObject()), mc.thePlayer.posZ + motionZ);
                        }
                    }
                    break;
                }
            }
        }


    }

    @EventTarget
    private void onJump(JumpEvent event) {
        event.setCancelled(true);
    }


    private double getHypixelSpeed(int stage) {
        double base = 0;

        final double init = moveUtils.getBaseMoveSpeed() + (moveUtils.getSpeedEffect() * 0.075);
        final double slowDown = init - (double) stage / 500.0 * 3;

        if (stage == 0) base = init;
        else if (stage >= 1) base = slowDown;

        return Math.max(base, moveUtils.getBaseMoveSpeed());
    }
}
