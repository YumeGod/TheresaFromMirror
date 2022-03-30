

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
    private final ModeValue offsetvalue = new ModeValue("Offset Value", "NCP", "NCP", "Mini", "Chill", "Negative", "Positive");
    private final BooleanValue packetsWhenNoMove = new BooleanValue("packets when no move", false);
    private final BooleanValue always = new BooleanValue("Always", false);
    private final BooleanValue nodelay = new BooleanValue("No Delay", false);

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
                    if (!(entity instanceof EntityLiving)) {
                    }
                }
            }
        }
    }

    public void onCrit(Entity entity) {
        if (!(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround))
            return;

        switch (mode.getCurrentMode()) {
            case "Packet":
                if (always.getObject() || entity.hurtResistantTime != 20) {
                    if (!i.hasReached(nodelay.getObject() ? 25 : 150)) return;
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
        double pos = 0;
        if (e.getEventType() == EventType.PRE) {
            switch (offsetvalue.getCurrentMode()) {
                case "NCP":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.0172422909396, .0182422909396),
                            ThreadLocalRandom.current().nextDouble(.00099158065143, .00109158065143)};
                    break;
                case "Mini":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.01832422909396, .02032422909396),
                            ThreadLocalRandom.current().nextDouble(.01032422909396, .01232422909396)};
                    break;
                case "Negative":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.00117, .00126),
                            ThreadLocalRandom.current().nextDouble(-.0001, -.00009),
                            ThreadLocalRandom.current().nextDouble(.01032422909396, .01232422909396),
                    };
                    break;
                case "Chill":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.0325, .0335),
                            ThreadLocalRandom.current().nextDouble(.00099158065143, .00109158065143),
                            ThreadLocalRandom.current().nextDouble(.0325, .0335),
                            ThreadLocalRandom.current().nextDouble(.00099158065143, .00109158065143)};
            break;
            case "Positive":
                offset = new double[]{
                        ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                        ThreadLocalRandom.current().nextDouble(.00317, .00526),
                        ThreadLocalRandom.current().nextDouble(-.0001, -.00009),
                        ThreadLocalRandom.current().nextDouble(.00099158065143, .00099158065143)
                };
                break;
        }

        if ("Recall".equalsIgnoreCase(mode.getCurrentMode())) {
            if (stage == 2) stage = 0;
            if (counter == offset.length) counter = 0;
            Speed speed = Main.INSTANCE.moduleManager.getModule(Speed.class);
            Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
            if (entity == null) return;
            if (mc.thePlayer.onGround) {
                if (always.getObject() || entity.hurtResistantTime != 20)
                    if (playerUtils.isMoving2()) {
                        if (stage < 2) {
                            if (speed.getState())
                                for (int i = 0; i < offset.length; i++) offset[i] = offset[i] * 1e-5;
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
            if (counter == offset.length) counter = 0;
            if (stage == 1) stage = 0;
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
                        if (speed.getState()) for (int i = 0; i < offset.length; i++) offset[i] = offset[i] * 1e-5;
                        e.setY(e.getY() + (offset[counter]));
                        e.setOnGround(false);
                        counter++;
                    }
            }
        }
    }

}
}


