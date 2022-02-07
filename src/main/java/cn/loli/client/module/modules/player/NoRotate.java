package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("NoRotate", "Just desync the rotation from serverside", ModuleCategory.PLAYER);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.rotationYaw != -180.0f && mc.thePlayer.rotationPitch != 0.0f) {
                mc.thePlayer.setPosition(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(), ((S08PacketPlayerPosLook) event.getPacket()).getZ());
                event.setCancelled(true);
            }
        }
    }

}
