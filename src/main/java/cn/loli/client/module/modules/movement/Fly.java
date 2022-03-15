

package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fly extends Module {

    private final BooleanValue enduring = new BooleanValue("Enduring", false);

    private final ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "Hypixel");

    private boolean jumped;
    private boolean clipped;
    boolean detect;

    double lastY;
    int stage, offset;

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
        stage = 0;
        offset = 0;
        detect = false;
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
                {
                    if (enduring.getObject()) {
                        switch (stage) {
                            case 0:
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), true));
                                for (int i = 0; i < 50; i++) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY() + 0.05, e.getZ(), false));
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), true));
                                }
                                lastY = e.getY();
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY(), e.getZ(), true));
                                stage++;
                                break;
                            case 1:
                                e.setOnGround(false);
                                e.setY(mc.thePlayer.posY + 0.05);
                                mc.thePlayer.setPosition(e.getX(), mc.thePlayer.posY + 0.05, e.getZ());
                                stage++;
                                break;
                            case 2:
                            case 3:
                            case 4:
                                e.setOnGround(false);
                                e.setY(mc.thePlayer.posY - 0.22);
                                stage++;
                                break;
                            default:
                                e.setOnGround(false);
                                break;
                        }
                    } else {
                        if (!jumped && mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.075;
                            jumped = true;
                            return;
                        }
                        if (mc.thePlayer.onGround && !clipped) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, e.getY() - 0.075, mc.thePlayer.posZ, true));
                            e.setY(e.getY() - 0.075);
                            e.setOnGround(true);
                            mc.thePlayer.setPosition(mc.thePlayer.posX, e.getY() + 0.075, mc.thePlayer.posZ);
                            clipped = true;
                        }

                        if (clipped) {
                            mc.thePlayer.motionY = 0.0;
                        }
                    }

                }
            }
        }
    }

    @EventTarget
    private void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof C03PacketPlayer && mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator()) {
            if (!((C03PacketPlayer) e.getPacket()).isMoving() && !((C03PacketPlayer) e.getPacket()).getRotating())
                e.setCancelled(true);
        }
    }

    @EventTarget
    private void onMove(PlayerMoveEvent e) {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator() && ((enduring.getObject() && stage >= 1) || (!enduring.getObject() && clipped))) {
            e.setY(mc.thePlayer.motionY = 0.0);
            moveUtils.setMotion(e, moveUtils.getBaseMoveSpeed(0.281, 0.2));
        } else {
            if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")){

            } else
                moveUtils.setMotion(e, 0);
        }
    }

    private void setEdit(MotionUpdateEvent event) {
        if (!jumped && mc.thePlayer.onGround) {
            for (int i = 0; i < 50; i++) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.05, mc.thePlayer.posZ, false));
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            }
            mc.thePlayer.motionY = 0.05f;
            jumped = true;
        }

        if (mc.thePlayer.onGround && !clipped) {
            clipped = true;
            return;
        }

        if (clipped) {
            mc.thePlayer.motionY = 0.0;
        }
    }
}
