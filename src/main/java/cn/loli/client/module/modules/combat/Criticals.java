

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.movement.Speed;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends Module {

    private enum MODE {
        EDIT("Edit"), PACKET("Packet"), RECALL("Recall");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty mode = new EnumProperty<>("Mode", MODE.PACKET);

    private enum OFFSET_VALUE {
        NCP("NCP"), MINI("Mini"), CHILL("Chill"), NEGATIVE("Negative"), POSITIVE("Positive"), ZERO("HLess");

        private final String name;

        OFFSET_VALUE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty offsetvalue = new EnumProperty<>("Offset Value", OFFSET_VALUE.NCP);
    private final BooleanProperty packetsWhenNoMove = new BooleanProperty("packets when no move", false);
    private final BooleanProperty always = new BooleanProperty("Always", false);
    private final BooleanProperty nodelay = new BooleanProperty("No Delay", false);

    int counter = 0, stage = 0;
    double[] offset = new double[3];
    TimeHelper i = new TimeHelper();

    public Criticals() {
        super("Criticals", "Makes you always deal a critical hit.", ModuleCategory.COMBAT);
    }


    private final IEventListener<MotionUpdateEvent> onEdit = e ->
    {
        double pos = 0;
        if (e.getEventType() == EventType.PRE) {
            switch (offsetvalue.getPropertyValue().toString()) {
                case "NCP":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.0172422909396, .0182422909396),
                            ThreadLocalRandom.current().nextDouble(.01032422909396, .01232422909396)};
                    break;
                case "Mini":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.0004187787, .0004516498),
                            ThreadLocalRandom.current().nextDouble(.01832422909396, .03032422909396),
                            ThreadLocalRandom.current().nextDouble(.0004187787, .0004516498)};
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
                            ThreadLocalRandom.current().nextDouble(.0525, .0535),
                            ThreadLocalRandom.current().nextDouble(.0004187787, .0004516498)};
                    break;
                case "Positive":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.00317, .00526),
                            ThreadLocalRandom.current().nextDouble(-.0001, -.00009),
                            ThreadLocalRandom.current().nextDouble(.0004187787, .0004516498)};
                    break;
                case "HLess":
                    offset = new double[]{
                            ThreadLocalRandom.current().nextDouble(.003032422909396, .007032422909396),
                            ThreadLocalRandom.current().nextDouble(.0004187787, .0004516498)};
                    break;
            }

            if ("Recall".equalsIgnoreCase(mode.getPropertyValue().toString())) {
                if (stage == 2) stage = 0;
                if (counter == offset.length) counter = 0;
                Speed speed = Main.INSTANCE.moduleManager.getModule(Speed.class);
                Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                if (entity == null) return;
                if (mc.thePlayer.onGround) {
                    if (always.getPropertyValue() || entity.hurtResistantTime != 20)
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

            if ("Edit".equals(mode.getPropertyValue().toString())) {
                if (counter == offset.length) counter = 0;
                if (stage == 1) stage = 0;
                Speed speed = Main.INSTANCE.moduleManager.getModule(Speed.class);
                Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                if (entity == null) return;
                if (mc.thePlayer.onGround) {
                    if (always.getPropertyValue() || entity.hurtResistantTime != 20)
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
    };


    public void onCrit(Entity entity) {
        if (!(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround))
            return;

        switch (mode.getPropertyValue().toString()) {
            case "Packet":
                if (always.getPropertyValue() || entity.hurtResistantTime != 20) {
                    if (!i.hasReached(nodelay.getPropertyValue() ? 25 : 150)) return;
                    for (double i : offset) sendPacket(i);
                    i.reset();
                }
                break;
            case "Edit":
            case "Recall":
                break;
            default:
                NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getPropertyValue(), 2));
        }
    }

    private void sendPacket(double yOffset) {
        C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ, false);
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }


}


