package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.rotation.RotationHook;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AlwaysRotate extends Module {
    public AlwaysRotate() {
        super("Packet Hook", "Hook the packet's motion", ModuleCategory.MISC);
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (((C03PacketPlayer) event.getPacket()).getRotating()) {
                RotationHook.prevYaw = RotationHook.yaw;
                RotationHook.prevPitch = RotationHook.pitch;
                RotationHook.yaw = ((C03PacketPlayer) event.getPacket()).getYaw();
                RotationHook.pitch = ((C03PacketPlayer) event.getPacket()).getPitch();
            }
        }
    }
}
