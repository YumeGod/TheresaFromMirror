package cn.loli.client.module.modules.movement;

import cn.loli.client.events.*;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.MoveUtils;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Speed extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Mini", "PacketAbusing", "Mini");
    private final NumberValue<Float> multiply = new NumberValue<>("Multiply", 1f, 1f, 2f);
    private final BooleanValue boost = new BooleanValue("Boost", true);


    double distance;
    private int stage;
    private double speed, less;
    private boolean lessSlow;

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

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        switch (modes.getCurrentMode()) {
            case "Mini": {
                if (PlayerUtils.isMoving2()) {
                    if (PlayerUtils.isInLiquid()) return;
                    if (boost.getObject()){
                        if (mc.thePlayer.isCollidedHorizontally) stage = -1;
                        less = Math.max(less - (less > 1 ? .12 : .11), 0);

                        if (PlayerUtils.isOnGround(0.01)) {
                            if (stage >= 0 || mc.thePlayer.isCollidedHorizontally) {
                                lessSlow = less++ > 1 && !lessSlow;
                                less = Math.min(less, 1.12);
                                stage = 0;
                            }
                        }
                        speed = getHypixelSpeed(stage) * 0.96;
                        if (stage < 0) speed = MoveUtils.getBaseMoveSpeed();
                        if (lessSlow) speed *= .97;
                        if (speed < MoveUtils.getBaseMoveSpeed()) speed = MoveUtils.getBaseMoveSpeed();

                    } else {
                        speed = MoveUtils.getBaseMoveSpeed();
                    }

                    MoveUtils.setMotion(event, speed);
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
                    if (PlayerUtils.isMoving2()) {
                        if (PlayerUtils.isInLiquid())
                            return;

                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.11D * multiply.getObject()), mc.thePlayer.posZ);
                      //      mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, event.getY() + (0.11D * multiply.getObject()) * 0.25, mc.thePlayer.posZ, false));

                            if (stage >= 75) stage = 0;
                            distance += (0.11D * multiply.getObject());
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

        final double init = MoveUtils.getBaseMoveSpeed() + (MoveUtils.getSpeedEffect() * 0.075);
        final double slowDown = init - (double) stage / 500.0 * 3;

        if (stage == 0) base = init;
        else if (stage >= 1) base = slowDown;

        if (mc.thePlayer.isCollidedHorizontally) {
            base = 0.2;
            if (stage == 0) {
                base = 0.0;
            }
        }

        return Math.max(base, (MoveUtils.getBaseMoveSpeed() + 0.014 * (double) MoveUtils.getSpeedEffect()));
    }
}
