

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Edit", "Edit", "Packet");
    private final ModeValue offsetvalue = new ModeValue("Offset Value", "NCP", "NCP", "Mini", "Negative", "Positive");
    private final BooleanValue packetsWhenNoMove = new BooleanValue("packets when no move", false);
    private final BooleanValue always = new BooleanValue("Always", false);
    int counter = 0;
    double[] offset = new double[3];

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

    public void onCrit(Entity entity) {
        if (!(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround))
            return;

        switch (mode.getCurrentMode()) {
            case "Packet":
                if (always.getObject() || entity.hurtResistantTime != 20)
                    for (double i : offset) sendPacket(i);
                break;
            case "Edit":
                break;
            default:
                NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getCurrentMode(), 2));
        }
    }

    private void sendPacket(double yOffset) {
        C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ, false);
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }


    @EventTarget
    public void onEdit(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE) {
            switch (offsetvalue.getCurrentMode()) {
                case "NCP":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.00332422909396, .00432422909396),
                            ThreadLocalRandom.current().nextDouble(9.0e-5d, 9.0e-5d * 4),
                            ThreadLocalRandom.current().nextDouble(.005032422909396, .006032422909396)};
                    break;
                case "Mini":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.01832422909396, .02032422909396),
                            ThreadLocalRandom.current().nextDouble(.01032422909396, .01232422909396),
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396)};
                    break;
                case "Negative":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.00317, .00526),
                            ThreadLocalRandom.current().nextDouble(9.0e-6d, 9.0e-6d * 4),
                            ThreadLocalRandom.current().nextDouble(.00217, .00326),
                            ThreadLocalRandom.current().nextDouble(9.0e-4d, 9.0e-4d * 2),
                    };
                    break;
                case "Positive":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(9.0e-3d, 9.0e-3d * 2),
                            ThreadLocalRandom.current().nextDouble(.00317, .00526),
                            ThreadLocalRandom.current().nextDouble(9.0e-2d, 9.0e-2d * 2)
                    };
                    break;
            }

            if ("Edit".equals(mode.getCurrentMode())) {
                if (counter == offset.length) counter = 0;
                Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                if (entity == null) return;
                if (mc.thePlayer.onGround) {
                    if (always.getObject() || entity.hurtResistantTime != 20)
                        if (playerUtils.isMoving2()) {
                            e.setY(e.getY() + (offset[counter]));
                            e.setOnGround(false);
                            counter++;
                        } else {
                            counter = 0;
                            if (packetsWhenNoMove.getObject() &&
                                    (entity.hurtResistantTime == 0 || entity.hurtResistantTime > 15))
                                for (double i : offset) sendPacket(i);

                        }
                }
            }
        }

    }
}


