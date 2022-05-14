

package cn.loli.client.module.modules.player;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayerSP;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class NoFall extends Module {

    private enum MODE {
        PACKET("Packet"), VULCAN("Vulcan") , EDIT("Edit") , COLLISION("Collision");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty mode = new EnumProperty<>("Mode", MODE.EDIT);

    private float fallDist, lastTickFallDist;

    private final BooleanProperty cancel = new BooleanProperty("Silent", false);
    private final BooleanProperty isVoid = new BooleanProperty("Ignore Void", false);

    public NoFall() {
        super("NoFall", "Negates fall damage.", ModuleCategory.PLAYER);
    }

    private final IEventListener<MotionUpdateEvent> onUpdate = event ->
    {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.capabilities.isFlying || mc.thePlayer.capabilities.disableDamage
                || mc.thePlayer.motionY >= 0.0d || mc.thePlayer.posY <= 0 || event.getEventType() == EventType.POST)
            return;

        if (mc.thePlayer.fallDistance == 0)
            fallDist = 0;

        fallDist += mc.thePlayer.fallDistance - lastTickFallDist;
        lastTickFallDist = mc.thePlayer.fallDistance;

        switch ( mode.getPropertyValue().toString()) {
            case "Packet":
                if (fallDist > 2F && (isVoid.getPropertyValue() || isBlockUnder())) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    fallDist *= .1;
                }
                break;
            case "Vulcan":
                // 随手写的，可以bypass, 如果不重置motionY会爆vClip flag.
                if (fallDist > 2F) {
                    double lastReportedY = ((IAccessorEntityPlayerSP) mc.thePlayer).getLastReportedPosY();
                    if (lastReportedY - Math.floor(lastReportedY) < 0.8F) {
                        event.setY(Math.floor(lastReportedY));
                        event.setOnGround(true);
                        fallDist = 0F;
                        mc.thePlayer.motionY = -0.0784000015258789;
                    }
                }
                break;
            case "Edit":
                if (fallDist > 2F && (isVoid.getPropertyValue() || isBlockUnder()))
                    event.setOnGround(true);
                break;
            case "Collision":
                if (fallDist > 3) {
                    if (cancel.getPropertyValue()) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
                                (mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2,
                                (mc.thePlayer.posY - (mc.thePlayer.posY % (1 / 64.0))),
                                (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2,
                                mc.thePlayer.rotationYaw,
                                mc.thePlayer.rotationPitch,
                                true), null);
                    } else {
                        mc.thePlayer.motionY = -(mc.thePlayer.posY - (mc.thePlayer.posY - (mc.thePlayer.posY % (1.0 / 64.0))));
                        event.setOnGround(true);
                    }
                    fallDist = 0;
                }
                break;
            default:
                NotificationManager.show(new Notification(NotificationType.WARNING, this.getName(), "Invalid mode: " + mode.getPropertyValue().toString(), 2));
                break;
        }

    };

    private final IEventListener<PacketEvent> onPacket = event ->
    {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer look = (C03PacketPlayer) event.getPacket();
            if (cancel.getPropertyValue() && mode.getPropertyValue().toString().equals("Packet"))
                if (look.getRotating() && look.isMoving())
                    if ((fallDist > 1) && isBlockUnder())
                        event.setPacket(new C03PacketPlayer.C04PacketPlayerPosition
                                (look.getPositionX(), look.getPositionY(), look.getPositionZ(), look.isOnGround()));

        }

    };


    public static boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0.0D) {
            return false;
        } else {
            int off = 0;

            while (true) {
                if (off >= (int) mc.thePlayer.posY + 2) {
                    return false;
                }

                AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0D, -off, 0.0D);
                if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                    return true;
                }

                off += 2;
            }
        }
    }
}
