package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S30PacketWindowItems;

public class NoSlowDown extends Module {

    public NoSlowDown() {
        super("NoSlowDown", "You wont get slowdown when you hold or eating", ModuleCategory.MOVEMENT);
    }

    @EventTarget
    public void onPost(MotionUpdateEvent event) {
        if (!mc.thePlayer.isUsingItem()) return;
        if (event.getEventType() == EventType.POST) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {

        if (event.getPacket() instanceof S30PacketWindowItems)
            event.setCancelled(mc.thePlayer.isUsingItem());

    }

}
