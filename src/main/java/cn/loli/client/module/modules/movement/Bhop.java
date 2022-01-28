package cn.loli.client.module.modules.movement;

import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.MoveUtils;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;

public class Bhop extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Tired", "Tired", "Motion1", "Motion2", "Motion3", "YPort", "CubeCraft", "PacketAbusing");


    public Bhop() {
        super("Speed", "Just You Speed Boost", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.thePlayer != null && mc.theWorld != null) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.02F);
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        switch (modes.getCurrentMode()) {
            case "Tired": {
                if (mc.thePlayer.motionY > -0.1) {
                    ((IAccessorKeyBinding) mc.gameSettings.keyBindSprint).setPressed(false);
                    if (mc.thePlayer.onGround && PlayerUtils.isMoving2()) {
                        mc.thePlayer.jump();
                        mc.thePlayer.setSprinting(false);
                        MoveUtils.setSpeed(0.06);
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 5F;
                    } else {
                        mc.thePlayer.setSprinting(true);
                        ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.23F);
                    }
                    if (mc.thePlayer.motionY >= 0.3) {
                        mc.thePlayer.motionY = 0;
                    }
                } else {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1F;
                }
                break;
            }
            case "Motion1": {
                if (PlayerUtils.isMoving2()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    } else {
                        mc.thePlayer.motionY -= 0.022;
                        mc.thePlayer.jumpMovementFactor = 0.032F;
                    }
                }
                break;
            }
            case "Motion2": {
                if (PlayerUtils.isMoving2()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.0201F);
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 0.94F;
                    }
                    if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
                        ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.02F);
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.8F;
                    }
                }
                break;
            }
            case "Motion3": {
                if (PlayerUtils.isMoving2()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                    }
                    if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.08F;
                    }
                }
                break;
            }
            case "YPort": {
                if (PlayerUtils.isMoving2())
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        mc.thePlayer.motionX *= 0.75;
                        mc.thePlayer.motionZ *= 0.75;
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 0.8F;
                    } else {
                        if (mc.thePlayer.motionY < 0.4) {
                            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.6F;
                            mc.thePlayer.motionY = -1337.0;
                            MoveUtils.setSpeed(0.26);
                        }
                    }
                break;
            }
            case "CubeCraft": {
                if (PlayerUtils.isMoving2())
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.4D;
                        MoveUtils.addMotion(0.2F, mc.thePlayer.rotationYaw);
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                    } else {
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.1F;
                        final double currentSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX
                                + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
                        final double speed = 1;
                        MoveUtils.setSpeed(speed * currentSpeed, mc.thePlayer.rotationYaw);
                        mc.thePlayer.motionY -= 0.00028;
                    }
                break;
            }
            case "PacketAbusing": {
                ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                if (mc.thePlayer.onGround) {
                    if (mc.thePlayer.ticksExisted % 20 == 0) {
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 15000.0F;
                    } else {
                        ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                    }
                }
                break;
            }
        }
    }

}
