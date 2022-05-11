package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class LongJump extends Module {

    private int stage;
    private double speed, verticalSpeed;

    public LongJump() {
        super("LongJump", "Jump more distance", ModuleCategory.MOVEMENT);
    }

    private final IEventListener<MotionUpdateEvent> onPre = e ->
    {
        if (e.getEventType() == EventType.PRE) {
            if (moveUtils.isOnGround(0.01) && stage == 0)
                fallDistDamage();
            verticalSpeed = moveUtils.getJumpHeight(mc.thePlayer) * 1.4f;
            speed = moveUtils.getBaseMoveSpeed(0.2831, 0.2) * 2.14;
            stage++;
        } else {
            if (mc.thePlayer.hurtTime > 0 && stage == 0) {

            }
        }
    };

    private final IEventListener<PlayerMoveEvent> onMove = e ->
    {
        if (stage > 0) {
            if (stage == 1) {
                speed *= 0.77;
            } else {
                speed *= 0.98;
            }
            e.setY(verticalSpeed);
            if (stage > 8) {
                verticalSpeed -= 0.032;
            } else {
                verticalSpeed *= 0.87;
            }
            stage++;

            if (moveUtils.isOnGround(0.01) && stage > 4)
                setState(false);

            moveUtils.setMotion(e, Math.max(moveUtils.getBaseMoveSpeed(0.2831, 0.2), speed));
        } else
            moveUtils.setMotion(e, 0);
    };




    @Override
    public void onEnable() {
        stage = 0;
    }


    public void fallDistDamage() {
        final double packets = Math.ceil(getMinFallDist() / 0.0625);
        for (int i = 0; i < 49; ++i) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0, mc.thePlayer.posZ, false));
        }
    }

    public double getMinFallDist() {
        double baseFallDist = 3.0;
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            baseFallDist += mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1.0f;
        }
        return baseFallDist;
    }
}
