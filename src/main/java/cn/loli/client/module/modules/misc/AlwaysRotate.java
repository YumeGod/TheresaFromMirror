package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.rotation.RotationHook;

import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AlwaysRotate extends Module {
    public AlwaysRotate() {
        super("Rotation Fix", "Hook the packet's motion", ModuleCategory.MISC);
    }

    private final IEventListener<PacketEvent> onPacket = event ->
    {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (((C03PacketPlayer) event.getPacket()).getRotating()){
                RotationHook.yaw = ((C03PacketPlayer) event.getPacket()).getYaw();
                RotationHook.pitch = ((C03PacketPlayer) event.getPacket()).getPitch();
            }
        }
    };

}
