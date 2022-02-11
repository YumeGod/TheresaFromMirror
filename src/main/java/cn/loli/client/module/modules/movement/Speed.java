package cn.loli.client.module.modules.movement;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.combat.Aura;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.MoveUtils;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.util.BlockPos;
import org.lwjgl.Sys;

import java.util.concurrent.ThreadLocalRandom;

public class Speed extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Mini", "PacketAbusing", "Mini");
    private final NumberValue<Float> multiply = new NumberValue<>("Multiply", 1f, 1f, 2.2f);

    private final BooleanValue crit = new BooleanValue("Fall Damage", true);

    double distance;

    public Speed() {
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
                    if (PlayerUtils.isInLiquid())
                        return;

                    MoveUtils.setMotion(event, MoveUtils.getBaseMoveSpeed());
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
                            if (Main.INSTANCE.moduleManager.getModule(Aura.class).target != null && crit.getObject()) {
                                int ht = Main.INSTANCE.moduleManager.getModule(Aura.class).target.hurtResistantTime;
                                switch (ht) {
                                    case 18:
                                    case 20: {
                                        event.setOnGround(false);
                                        event.setY(mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(0.0019, 0.0091921599284565));
                                        break;
                                    }
                                    case 17:
                                    case 19: {
                                        event.setOnGround(false);
                                        event.setY(mc.thePlayer.posY + ThreadLocalRandom.current().nextDouble(1.5E-4, 1.63166800276E-4));
                                        break;
                                    }
                                }
                            }

                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.11D * multiply.getObject()), mc.thePlayer.posZ);
                            distance += (0.11D * multiply.getObject());
                        }

                    }
                    break;
                }
            }
        }


    }

}
