package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Sneak extends Module {

    boolean isSneaking = false;

    public Sneak() {
        super("Sneak", "Always Sneak", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        if (isSneaking) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }
    }

    @EventTarget
    private void onMotion(final MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            if (isSneaking) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = !isSneaking;
            }
        } else if (event.getEventType() == EventType.POST) {
            if (!isSneaking) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                isSneaking = !isSneaking;
            }
        }
    }

}
