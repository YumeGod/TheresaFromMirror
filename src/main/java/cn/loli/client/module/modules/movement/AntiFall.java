package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AntiFall extends Module {

    private final NumberValue<Integer> falldistance = new NumberValue<>("Fall Distance", 3, 1, 6);
    private final BooleanValue matrix = new BooleanValue("Matrix", false);

    public AntiFall() {
        super("AntiFall", "I am the first one to fixz antifalls maybe", ModuleCategory.MOVEMENT);
    }

    @EventTarget
    public void onFall(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            if (mc.thePlayer.fallDistance > falldistance.getObject() && playerUtils.isOverVoid(mc)) {
                if (matrix.getObject()) {
                    if ((mc.thePlayer.motionY + mc.thePlayer.posY) < Math.floor(mc.thePlayer.posY)) {
                        mc.thePlayer.motionY = Math.floor(mc.thePlayer.posY) - mc.thePlayer.posY;

                        if (mc.thePlayer.motionY == 0)
                            event.setOnGround(true);
                    }
                } else {
                    if (mc.thePlayer.ticksExisted % 2 == 0) {
                        event.setX(event.getX() + 0.2 + Math.random() / 100);
                        event.setZ(event.getZ() - 0.2 + Math.random() / 100);
                    } else {
                        event.setX(event.getX() - 0.2 + Math.random() / 100);
                        event.setZ(event.getZ() + 0.2 + Math.random() / 100);
                    }
                }

            }
        }

    }

    public void blinkTo(double speed, float yaw) {
        this.blinkTo(speed, mc.thePlayer.posY + 1e-31, yaw, mc.thePlayer.onGround);
    }

    public void blinkTo(double speed, double y, float yaw, boolean ground) {
        double motionX = -Math.sin(Math.toRadians(getDirection(yaw))) * speed;
        double motionZ = Math.cos(Math.toRadians(getDirection(yaw))) * speed;
        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + motionX, y, mc.thePlayer.posZ + motionZ, ground));
    }

    public float getDirection(float rotationYaw) {
        float left = mc.gameSettings.keyBindLeft.isPressed() ? mc.gameSettings.keyBindBack.isPressed() ? 45 : mc.gameSettings.keyBindForward.isPressed() ? -45 : -90 : 0;
        float right = mc.gameSettings.keyBindRight.isPressed() ? mc.gameSettings.keyBindBack.isPressed() ? -45 : mc.gameSettings.keyBindForward.isPressed() ? 45 : 90 : 0;
        float back = mc.gameSettings.keyBindBack.isPressed() ? +180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }
}
