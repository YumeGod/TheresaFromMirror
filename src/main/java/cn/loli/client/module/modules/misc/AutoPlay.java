package cn.loli.client.module.modules.misc;

import cn.loli.client.events.ChatEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.IChatComponent;

public class AutoPlay extends Module {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay Second", 3, 1, 6);

    public AutoPlay() {
        super("AutoPlay", "Auto play the next game", ModuleCategory.MISC);
    }

    @EventTarget
    private void onPacket(ChatEvent event) {
        for (IChatComponent cc : event.getChatComponent().getSiblings()) {
            final ClickEvent ce = cc.getChatStyle().getChatClickEvent();
            if (ce != null)
                if ((ce.getAction() == ClickEvent.Action.RUN_COMMAND) && ce.getValue().contains("/play")) {
                    NotificationManager.show(new Notification(NotificationType.INFO, this.getName(), "Play again in " + delay.getObject() + "s", 2));
                    new Thread(() -> {
                        try {
                            Thread.sleep(delay.getObject() * 1000L);
                        } catch (final InterruptedException a) {
                            a.printStackTrace();
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(ce.getValue()));
                    }).start();

                    event.setCancelled(true);
                }
        }
    }
}
