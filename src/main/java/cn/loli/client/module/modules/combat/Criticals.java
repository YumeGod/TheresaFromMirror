

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Edit", "Edit");
    //   private final NumberValue<Integer> cpc = new NumberValue<>("Hit", 4, 1, 10);
    int counter = 0;

    public Criticals() {
        super("Criticals", "Makes you always deal a critical hit.", ModuleCategory.COMBAT);
    }

    @EventTarget
    public void onPacket(PacketEvent e) {
        if (e.getEventType() == EventType.SEND) {
            if (e.getPacket() instanceof C02PacketUseEntity) {
                if (((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    Entity entity = ((C02PacketUseEntity) e.getPacket()).getEntityFromWorld(mc.theWorld);
                    if (!(entity instanceof EntityLiving)) return;
                }
            }
        }
    }

    public void onCrit() {
        if (!(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround))
            return;

        switch (mode.getCurrentMode()) {
            case "Packet": {
                sendPacket(0.004 + ThreadLocalRandom.current().nextDouble(0.001));
                sendPacket(0.001 + ThreadLocalRandom.current().nextDouble(0.001));
                break;
            }
            case "Edit": {
                break;
            }
            default:
                NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getCurrentMode(), 2));
        }
    }

    private void sendPacket(double yOffset) {
        C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ, false);
    }


    @EventTarget
    public void onEdit(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE)
            if ("Edit".equals(mode.getCurrentMode()))
                if (playerUtils.isMoving2()) {
                    double[] offset = {ThreadLocalRandom.current().nextDouble(.01032422909396, .02032422909396),
                            ThreadLocalRandom.current().nextDouble(.005032422909396, .010032422909396),
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .005032422909396)};
                    if (counter == 3) counter = 0;
                    Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                    if (entity == null) return;
                    if (mc.thePlayer.onGround) {
                        e.setY(e.getY() + (offset[counter]));
                        e.setOnGround(false);
                        counter++;
                    } else
                        counter = 0;
                }
    }
}


