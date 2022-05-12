

package dev.xix.feature.impl.combat;

import cn.loli.client.events.MotionUpdateEvent;
import dev.xix.TheresaClient;
import dev.xix.feature.module.AbstractTheresaModule;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.TheresaModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends AbstractTheresaModule {
    private final ModeValue mode = new ModeValue("Mode", "Edit", "Edit", "Packet", "Recall");
    private final ModeValue offsetvalue = new ModeValue("Offset Value", "NCP", "NCP", "Mini", "Chill", "Negative", "Positive", "HLess");
    private final BooleanValue packetsWhenNoMove = new BooleanValue("packets when no move", false);
    private final BooleanValue always = new BooleanValue("Always", false);
    private final BooleanValue nodelay = new BooleanValue("No Delay", false);

    int counter = 0, stage = 0;
    double[] offset = new double[3];
    TimeHelper i = new TimeHelper();

    public Criticals() {
        super("Criticals", TheresaModuleCategory.COMBAT);
    }


    private final IEventListener<MotionUpdateEvent> onEdit = e ->
    {
        double pos = 0;
        if (e.getEventType() == EventType.PRE) {
            switch (offsetvalue.getCurrentMode()) {
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

        }
    };


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


}


