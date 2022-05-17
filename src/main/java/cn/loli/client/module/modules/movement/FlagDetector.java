

package cn.loli.client.module.modules.movement;

import cn.loli.client.Main;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;


public class FlagDetector extends Module {
    private final List<Vec3> lastLocations = new ArrayList<>();
    private final List<Long> lastSetBacks = new ArrayList<>();

    public FlagDetector() {
        super("FlagDetector", "Detects flags/violations/setbacks", ModuleCategory.MOVEMENT);
    }

    private final IEventListener<MotionUpdateEvent> onMove = event ->
    {
        if (event.getEventType() != EventType.POST) return;

        List<Long> remove = new ArrayList<>();

        for (Long lastSetBack : lastSetBacks) {
            if (System.currentTimeMillis() - lastSetBack > 5000) {
                remove.add(lastSetBack);
            }
        }
        for (Long aLong : remove) {
            lastSetBacks.remove(aLong);
        }

        lastLocations.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

        while (lastLocations.size() > 30) {
            lastLocations.remove(0);
        }
    };

    private final IEventListener<PacketEvent> onPacket = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook p = (S08PacketPlayerPosLook) event.getPacket();
            boolean setback = lastLocations.stream().anyMatch(loc -> p.getX() == loc.xCoord && p.getY() == loc.yCoord && p.getZ() == loc.zCoord);

            if (setback) {
                lastSetBacks.add(System.currentTimeMillis());
                if (lastSetBacks.size() < 3){
                    Main.INSTANCE.moduleManager.getModule(Speed.class).setState(false);
                    NotificationManager.show(new Notification(NotificationType.WARNING, getName(), "Setback detected", 1));
                }
            }
        }
    };

}
