

package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fly extends Module {

    private final BooleanValue enduring = new BooleanValue("Enduring", false);

    //TODO : Use Damage to make fly further? idk.
    private final BooleanValue damage = new BooleanValue("Damage", false);

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

    private final IEventListener<MotionUpdateEvent> onMotion = e ->
    {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator()) {
            if (e.getEventType() == EventType.PRE) {
                {
                    if (enduring.getObject()) {
                        switch (stage) {
                            case 0:
                                if (damage.getObject())
                                    moveUtils.getDamage(mc.thePlayer);

                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                                lastY = e.getY();
                                stage++;
                                break;
                            case 1:
                                e.setOnGround(true);
                                e.setY(mc.thePlayer.posY + 0.0625);
                                mc.thePlayer.setPosition(e.getX(), mc.thePlayer.posY + 0.0625, e.getZ());
                                stage++;
                                break;
                            case 2:
                                e.setOnGround(false);
                                e.setY(mc.thePlayer.posY - 0.1425);
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
    };


    private final IEventListener<PlayerMoveEvent> onMove = e ->
    {
        if (mode.getCurrentMode().equalsIgnoreCase("Hypixel") && !mc.thePlayer.isSpectator() && ((enduring.getObject() && stage >= 1) || (!enduring.getObject() && clipped))) {
            e.setY(mc.thePlayer.motionY = 0.0);
            moveUtils.setMotion(e, moveUtils.getBaseMoveSpeed(0.2871, 0.2));
        } else {
            if (mode.getCurrentMode().equalsIgnoreCase("Vanilla")) {

            } else
                moveUtils.setMotion(e, 0);
        }
    };


}
