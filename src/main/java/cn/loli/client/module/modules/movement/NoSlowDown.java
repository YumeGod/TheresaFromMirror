package cn.loli.client.module.modules.movement;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S30PacketWindowItems;
import tv.twitch.chat.Chat;

import java.util.Arrays;
import java.util.Random;

public class NoSlowDown extends Module {

    private final BooleanValue slot = new BooleanValue("Switch", false);
    private final BooleanValue itemswitch = new BooleanValue("New", false);

    public NoSlowDown() {
        super("NoSlowDown", "You wont get slowdown when you hold or eating", ModuleCategory.MOVEMENT);
    }

    @EventTarget
    public void onMotion(MotionUpdateEvent event) {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if (!mc.thePlayer.isUsingItem()) return;

        if (event.getEventType() == EventType.POST) {
            final int curSlot = mc.thePlayer.inventory.currentItem;
            final int spoof = curSlot == 0 ? 1 : -1;
            if (slot.getObject()){
                if (itemswitch.getObject())
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot + spoof));

                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot));
            }
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S30PacketWindowItems)
            event.setCancelled(mc.thePlayer.isUsingItem());

    }

}
