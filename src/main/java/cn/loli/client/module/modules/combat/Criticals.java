

package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.modules.movement.Speed;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Criticals extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Packet", "Packet", "Edit", "Hover", "Hypixel");
    private final NumberValue<Integer> cpc = new NumberValue<>("Hit", 4, 1, 10);
    int counter;

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
                    counter++;
                }
            }
        }
    }

    public void onCrit() {
        if (!(mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround))
            return;

        switch (mode.getCurrentMode()) {
            case "Packet": {
                sendPacket(0.012 + ThreadLocalRandom.current().nextDouble(1.0E-4, 9.0E-4));
                sendPacket(0.005 + ThreadLocalRandom.current().nextDouble(0.001));
                break;
            }
            case "Hover": {
                sendPacket(0.001);
                sendPacket(-0.0075);
                sendPacket(0.01);
                break;
            }
            case "Edit": {
                sendPacket(0);
                sendPacket(0.075 + ThreadLocalRandom.current().nextDouble(0.008) * (new Random().nextBoolean() ? 0.98 : 0.99) + mc.thePlayer.ticksExisted % 0.0215 * 0.94);
                sendPacket((new Random().nextBoolean() ? 0.01063469198817 : 0.013999999) * (new Random().nextBoolean() ? 0.98 : 0.99));
                break;
            }
            case "Hypixel": {
                break;
            }
            default:
                NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getCurrentMode(), 2));
        }
    }

    private void sendPacket(double yOffset) {
        C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ, false);

        if (counter % cpc.getObject() == 0)
            mc.thePlayer.sendQueue.addToSendQueue(packet);
    }


    @EventTarget
    public void onEdit(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE)
            if ("Hypixel".equals(mode.getCurrentMode()))
                if (PlayerUtils.isMoving2()) {
                    Entity entity = Main.INSTANCE.moduleManager.getModule(Aura.class).target;
                    if (entity == null) return;
                    if (mc.thePlayer.onGround && entity.hurtResistantTime != 20) {
                        e.setY(e.getY() + 0.003);
                        if (mc.thePlayer.ticksExisted % 10 == 0)
                            e.setY(e.getY() + 0.001);

                        e.setOnGround(false);
                    }
                }
    }
}


