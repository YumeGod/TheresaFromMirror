package cn.loli.client.module.modules.misc;

import cn.loli.client.events.ChatEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.IChatComponent;

public class AutoPlay extends Module {

    private final NumberProperty<Integer> delay = new NumberProperty<>("Delay Second", 3, 1, 6 , 1);

    public AutoPlay() {
        super("AutoPlay", "Auto play the next game", ModuleCategory.MISC);
    }

    private final IEventListener<ChatEvent> onPacket = e ->
    {
        for (IChatComponent cc : e.getChatComponent().getSiblings()) {
            final ClickEvent ce = cc.getChatStyle().getChatClickEvent();
            if (ce != null)
                if ((ce.getAction() == ClickEvent.Action.RUN_COMMAND) && ce.getValue().contains("/play")) {
                    NotificationManager.show(new Notification(NotificationType.INFO, this.getName(), "Play again in " + delay.getPropertyValue() + "s", 2));
                    new Thread(() -> {
                        try {
                            Thread.sleep(delay.getPropertyValue() * 1000L);
                        } catch (final InterruptedException a) {
                            a.printStackTrace();
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(ce.getValue()));
                    }).start();

                    e.setCancelled(true);
                }
        }
    };

}
