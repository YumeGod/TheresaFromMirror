

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

    private final BooleanValue edit = new BooleanValue("Edit", true);
    private final BooleanValue enduring = new BooleanValue("Enduring", false);

    private final ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla", "Hypixel");

    private boolean jumped;
    private boolean clipped;

    int stage;

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
                    if (!jumped && mc.thePlayer.onGround) {
                        if (enduring.getObject()) {
                            for (int i = 0; i < 30; i++) {
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                            }
                            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.075, mc.thePlayer.posZ, false));
                        }
                        mc.thePlayer.motionY = 0.075;
                        jumped = true;
                        return;
                    }
                    if (mc.thePlayer.onGround && !clipped) {
                        e.setY(e.getY() - 0.075);
                        e.setOnGround(true);
                        clipped = true;
                   //     mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.075, mc.thePlayer.posZ);
                    }

                    if (clipped) {
                        mc.thePlayer.motionY = 0.0;
                    }
                }
            }
        }
    }

    @EventTarget
    private void onPacket(PacketEvent e) {
    }

    @EventTarget
    private void onMove(PlayerMoveEvent e) {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator() && clipped) {
            e.setY(mc.thePlayer.motionY = 0.0);
            moveUtils.setMotion(e, moveUtils.getBaseMoveSpeed(0.281, 0.2));
        } else
            moveUtils.setMotion(e, 0);
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
