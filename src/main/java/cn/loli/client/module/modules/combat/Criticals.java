

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.movement.Speed;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;
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
    private final ModeValue mode = new ModeValue("Mode", "Edit", "Edit", "Packet", "Recall");
    private final ModeValue offsetvalue = new ModeValue("Offset Value", "NCP", "NCP", "Mini", "Less", "Negative", "Positive");
    private final BooleanValue packetsWhenNoMove = new BooleanValue("packets when no move", false);
    private final BooleanValue always = new BooleanValue("Always", false);
    int counter = 0, stage = 0;
    double[] offset = new double[3];
    TimeHelper i = new TimeHelper();

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
                if (always.getObject() || entity.hurtResistantTime != 20){
                    if (!i.hasReached(350)) return;
                    for (double i : offset) sendPacket(i);
                    i.reset();
                }
                break;
            case "Edit":
            case "Recall":
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
                            ThreadLocalRandom.current().nextDouble(.0675, .0775),
                            ThreadLocalRandom.current().nextDouble(.0172422909396, .0182422909396),
                            ThreadLocalRandom.current().nextDouble(.0112422909396, .0122422909396)};
                    break;
                case "Mini":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.01832422909396, .02032422909396),
                            ThreadLocalRandom.current().nextDouble(.01032422909396, .01232422909396)};
                    break;
                case "Negative":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.00317, .00326),
                            ThreadLocalRandom.current().nextDouble(.0001, .0002),
                            ThreadLocalRandom.current().nextDouble(.00417, .00526),
                    };
                    break;
                case "Less":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.016, .018),
                            ThreadLocalRandom.current().nextDouble(.0325, .0335),
                    };
                    break;
                case "Positive":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.00317, .00526),
                            ThreadLocalRandom.current().nextDouble(.0009, .005)
                    };
                    break;
            }


            if ("Recall".equalsIgnoreCase(mode.getCurrentMode())) {
                if (i.hasReached(350) && stage == 2) {
                    stage = 0;
                    i.reset();
                }
                if (counter == offset.length) counter = 0;
                Speed speed = Main.INSTANCE.moduleManager.getModule(Speed.class);
                Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                if (entity == null) return;
                if (mc.thePlayer.onGround) {
                    if (always.getObject() || entity.hurtResistantTime != 20)
                        if (playerUtils.isMoving2()) {
                            if (stage < 2) {
                                if (speed.getState()) return;
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(e.getX(), e.getY() + offset[counter], e.getZ(), false));
                                counter++;
                                e.setY(e.getY() + (offset[counter]));
                                e.setOnGround(false);
                                counter++;
                                stage++;
                            }
                        } else {
                            counter = 0;
                        }
                    else
                        counter = 0;
                }
            }

            if ("Edit".equals(mode.getCurrentMode())) {
                if (counter == offset.length) {
                    counter = 0;
                }
                if (i.hasReached(350) && stage == 1) {
                    stage = 0;
                    i.reset();
                }
                Speed speed = Main.INSTANCE.moduleManager.getModule(Speed.class);
                Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                if (entity == null) return;
                if (mc.thePlayer.onGround) {
                    if (always.getObject() || entity.hurtResistantTime != 20)
                        if (playerUtils.isMoving2()) {
                            if (counter == offset.length) {
                                stage = 1;
                                return;
                            }

                            if (speed.getState() || stage == 1) return;
                            e.setY(e.getY() + (offset[counter]));
                            e.setOnGround(false);
                            counter++;
                        }
                }
            }
        }

    }
}


