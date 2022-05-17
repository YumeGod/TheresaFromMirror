package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.injection.mixins.IAccessorNetHandlerPlayClient;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.ChatComponentText;

import java.util.ConcurrentModificationException;

public class AntiVanish extends Module {

    public AntiVanish() {
        super("AntiVanish", "You can see if a player is vanished", ModuleCategory.MISC);
    }

    private final IEventListener<PacketEvent> onVanish = event ->
    {
        if (event.getPacket() instanceof S38PacketPlayerListItem) {
            if (((S38PacketPlayerListItem) event.getPacket()).getAction() == S38PacketPlayerListItem.Action.UPDATE_LATENCY)
                for (S38PacketPlayerListItem.AddPlayerData addPlayerData : ((S38PacketPlayerListItem) event.getPacket()).getEntries())
                    if (!((IAccessorNetHandlerPlayClient)mc.getNetHandler()).getPlayerInfoMap().containsKey(addPlayerData.getProfile().getId())) {
                        final ChatComponentText text = new ChatComponentText("A player is vanished!");
                        text.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + addPlayerData.getProfile().getId().toString()));
                        try {
                            mc.thePlayer.addChatMessage(text);
                        } catch (ConcurrentModificationException e) {
                            e.printStackTrace();
                        }
                    }
        }
    };


}
