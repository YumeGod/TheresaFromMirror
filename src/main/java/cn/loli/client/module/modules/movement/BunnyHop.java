package cn.loli.client.module.modules.movement;

import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class BunnyHop extends Module {

    private final BooleanValue strafe = new BooleanValue("Ice Boost", false);


    public BunnyHop() {
        super("BHop", "More Legit one *maybe*", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    private void onHop(TickEvent event) {
        if (mc.thePlayer.isSneaking() || mc.thePlayer.isInWater() || game.keyBindJump.isPressed() || game.keyBindBack.isPressed())
            return;

        if (game.keyBindForward.isPressed() || game.keyBindLeft.isPressed() || game.keyBindRight.isPressed()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            } else {
                if (strafe.getObject()) {
                    getStrafe(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ));
                }
            }
        }
    }

    private void getStrafe(double speed) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        double yaw = player.rotationYaw;
        boolean isMoving = player.moveForward != 0 || player.moveStrafing != 0;
        boolean isMovingForward = player.moveForward > 0;
        boolean isMovingBackward = player.moveForward < 0;
        boolean isMovingRight = player.moveStrafing > 0;
        boolean isMovingLeft = player.moveStrafing < 0;
        boolean isMovingSideways = isMovingLeft || isMovingRight;
        boolean isMovingStraight = isMovingForward || isMovingBackward;

        if (isMoving) {
            if (isMovingForward && !isMovingSideways) {
                yaw += 0;
            } else if (isMovingBackward && !isMovingSideways) {
                yaw += 180;
            } else if (isMovingForward && isMovingLeft) {
                yaw += 45;
            } else if (isMovingForward) {
                yaw -= 45;
            } else if (!isMovingStraight && isMovingLeft) {
                yaw += 90;
            } else if (!isMovingStraight && isMovingRight) {
                yaw -= 90;
            } else if (isMovingBackward && isMovingLeft) {
                yaw += 135;
            } else if (isMovingBackward) {
                yaw -= 135;
            }

            yaw = Math.toRadians(yaw);
            player.motionX = -Math.sin(yaw) * speed;
            player.motionZ = Math.cos(yaw) * speed;
        }
    }
}
