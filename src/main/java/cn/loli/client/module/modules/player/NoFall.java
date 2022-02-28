

package cn.loli.client.module.modules.player;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayerSP;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class NoFall extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Packet", "Packet", "Vulcan");

    private final BooleanValue cancel = new BooleanValue("Slient", false);

    public NoFall() {
        super("NoFall", "Negates fall damage.", ModuleCategory.PLAYER);
    }

    @EventTarget
    private void onUpdate(MotionUpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
                || mc.thePlayer.motionY >= 0.0d || mc.thePlayer.posY <= 0 || event.getEventType() == EventType.POST)
            return;

        if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
            if (mc.thePlayer.fallDistance > 2F && isBlockUnder()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                mc.thePlayer.fallDistance *= .1;
            }
        } else if (mode.getCurrentMode().equalsIgnoreCase("Vulcan")) {
            // 随手写的，可以bypass, 如果不重置motionY会爆vClip flag.
            if (mc.thePlayer.fallDistance > 2F) {
                double lastReportedY = ((IAccessorEntityPlayerSP) mc.thePlayer).getLastReportedPosY();
                if (lastReportedY - Math.floor(lastReportedY) < 0.8F) {
                    event.setY(Math.floor(lastReportedY));
                    event.setOnGround(true);
                    mc.thePlayer.fallDistance = 0F;
                    mc.thePlayer.motionY = -0.0784000015258789;
                }
            }
        } else {
            NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getCurrentMode(), 2));
        }
    }

    @EventTarget
    private void onPacket(PacketEvent event) {

        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer look = (C03PacketPlayer) event.getPacket();
            if (cancel.getObject())
                if (look.getRotating() && look.isMoving())
                    if ((mc.thePlayer.fallDistance > 1) && isBlockUnder())
                        event.setPacket(new C03PacketPlayer.C04PacketPlayerPosition
                                (look.getPositionX(), look.getPositionY(), look.getPositionZ(), look.isOnGround()));

        }

    }

    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0D) {
            return false;
        } else {
            int off = 0;

            while (true) {
                if (off >= (int) mc.thePlayer.posY + 2) {
                    return false;
                }

                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0D, -off, 0.0D);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }

                off += 2;
            }
        }
    }
}
