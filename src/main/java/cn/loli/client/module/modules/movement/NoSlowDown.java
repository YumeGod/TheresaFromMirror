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
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S30PacketWindowItems;

public class NoSlowDown extends Module {
    private final ModeValue mode = new ModeValue("Mode","Packet","Vanilla" ,"Packet" ,"Tweak");
    private final BooleanValue itemswitch = new BooleanValue("New", false);
    private final BooleanValue onlyOnMove = new BooleanValue("Only on move", false);

    public NoSlowDown() {
        super("NoSlowDown", "You wont get slowdown when you hold or eating", ModuleCategory.MOVEMENT);
    }

    private final IEventListener<MotionUpdateEvent> onMotion = e ->
    {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if (!mc.thePlayer.isUsingItem() || (mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) || (onlyOnMove.getObject() && !playerUtils.isMoving2()))
            return;

        switch (mode.getCurrentMode()) {
            case "Packet":
                doPacketMode(e);
                break;
            case "Tweak":
                if (mc.thePlayer.ticksExisted % 2 != 0) return;
                doPacketMode(e);
            case "Vanilla":
            default:
                break;
        }
    };

    private final IEventListener<PacketEvent> onPacket = e ->
    {
        if (e.getPacket() instanceof S30PacketWindowItems)
            e.setCancelled(mc.thePlayer.isUsingItem());
    };


    private void doPacketMode(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            final int curSlot = mc.thePlayer.inventory.currentItem;
            final int spoof = curSlot == 0 ? 1 : -1;
            if (itemswitch.getObject())
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot + spoof));

            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(curSlot));
        }

        if (event.getEventType() == EventType.POST) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        }
    }

}
