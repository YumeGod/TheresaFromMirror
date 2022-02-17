

package cn.loli.client.module.modules.player;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorEntityPlayerSP;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Packet", "Packet", "Vulcan");

    public NoFall() {
        super("NoFall", "Negates fall damage.", ModuleCategory.PLAYER);
    }

    @EventTarget
    public void onUpdate(MotionUpdateEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
                || mc.thePlayer.motionY >= 0.0d || mc.thePlayer.posY <= 0 || event.getEventType() == EventType.POST)
            return;

        if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
            if (mc.thePlayer.fallDistance > 2F) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                mc.thePlayer.fallDistance = 0F;
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
}
