

package cn.loli.client.module.modules.player;

import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Packet", "Packet");

    public NoFall() {
        super("NoFall", "Negates fall damage.", ModuleCategory.PLAYER);
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (mode.getCurrentMode().equalsIgnoreCase("Packet")) {
            if (mc.thePlayer.fallDistance > 2F) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            }
        } else {
            NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getCurrentMode(), 2));
        }
    }
}
