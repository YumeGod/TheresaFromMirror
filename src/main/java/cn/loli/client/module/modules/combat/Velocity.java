package cn.loli.client.module.modules.combat;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.implementations.IS27PacketExplosion;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    private final NumberValue<Integer> horizon = new NumberValue<>("Horizon", 80, 0, 100);
    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 80, 0, 100);
    private final BooleanValue explosion = new BooleanValue("Explosion", true);
    private final BooleanValue legit = new BooleanValue("Jump", false);


    public Velocity() {
        super("Velocity", "Reduce your knock-back", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable(){
        super.onEnable();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @EventTarget
    private void onJump(UpdateEvent e){
        if (legit.getObject()){
            if (mc.thePlayer.hurtTime == 10 && mc.thePlayer.onGround) {
               mc.thePlayer.jump();
            }
        }
    }
    @EventTarget
    private void onPacket(PacketEvent event) {
        float hor = horizon.getObject() / 100f;
        float ver = vertical.getObject() / 100f;

        if (legit.getObject())
            return;

        if (event.getPacket() instanceof S27PacketExplosion && explosion.getObject()) {
            S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
            if (horizon.getObject() == 0 && vertical.getObject() == 0) {
                event.setCancelled(true);
                return;
            }

            ((IS27PacketExplosion) packet).setX(packet.func_149149_c() * hor);
            ((IS27PacketExplosion) packet).setY(packet.func_149144_d() * ver);
            ((IS27PacketExplosion) packet).setZ(packet.func_149147_e() * hor);
        }

        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
               // ChatUtils.send("Received A KB Packet");
                event.setCancelled(true);

                if (horizon.getObject() == 0 && vertical.getObject() == 0)
                    return;

                mc.thePlayer.setVelocity(packet.getMotionX() / 8000d * hor,
                        packet.getMotionY() / 8000d * ver,
                        packet.getMotionZ() / 8000d * hor);
            }
        }

    }

}
