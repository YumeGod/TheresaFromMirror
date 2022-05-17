package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.player.NoRotate;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Abuser extends Module {

    private final BooleanProperty fly = new BooleanProperty("OldVerus-Fly-Bypass", false);
    private final BooleanProperty range = new BooleanProperty("Simple-Reach-Bypass", false);
    private final BooleanProperty ncp = new BooleanProperty("NCP-Timer-Flag", false);
    private final BooleanProperty redesky = new BooleanProperty("Rede-Sky-Semi", false);
    private final BooleanProperty hypixel = new BooleanProperty("Hypixel-Semi", false);
    public final BooleanProperty packetMeme = new BooleanProperty("Hypixel-Meme", false);
    public final BooleanProperty packetMemeEdit = new BooleanProperty("Hypixel-Meme-Transform", false);
    private final BooleanProperty packetFreeze = new BooleanProperty("Hypixel-Freeze", false);
    public final BooleanProperty updateFreeze = new BooleanProperty("Hypixel-Freeze-Update", true);
    private final BooleanProperty packetBrust = new BooleanProperty("Brust", false);
    private final BooleanProperty lesspacket = new BooleanProperty("Less-Packet", false);
    private final BooleanProperty packetDormant = new BooleanProperty("Position-Dormant", false);
    private final NumberProperty<Integer> collectTimer = new NumberProperty<>("Dormant-Collect-Timer", 10, 1, 30 , 1);
    private final NumberProperty<Integer> dormantLatency = new NumberProperty<>("Dormant-Collect-Latency", 4, 1, 20 , 1);
    private final NumberProperty<Integer> freezeLatency = new NumberProperty<>("Freeze-Collect-Latency", 15, 10, 50 , 1);
    private final BooleanProperty positionSpoof = new BooleanProperty("Position-Spoof", false);
    private final BooleanProperty sneak = new BooleanProperty("Sprint-Spoof", false);

    public boolean hasDisable;
    public double x, y, z;
    public final ConcurrentLinkedQueue<PosLookPacket> flagStock = new ConcurrentLinkedQueue<>();
    public final TimeHelper timer = new TimeHelper(), freezeTimer = new TimeHelper(), resetTimer = new TimeHelper();
    private final TimeHelper burst = new TimeHelper(), dormantTimer = new TimeHelper(), choke = new TimeHelper();
    private final List<Packet<INetHandlerPlayClient>> packets = new ArrayList<>();
    private final List<Packet<?>> dormant = new ArrayList<>();

    long delay = 150;
    long dormantDelay = 200;
    int invalid = 0;

    public Abuser() {
        super("Abuser", "Abuse Something which you can bypass some anti cheat", ModuleCategory.MISC);
    }


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    private final IEventListener<PacketEvent> onPost = event ->
    {
        if (mc.isSingleplayer()) return;

        if (range.getPropertyValue()) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (((C0FPacketConfirmTransaction) event.getPacket()).getWindowId() >= 0)
                    event.setCancelled(true);
            }
        }

        if (sneak.getPropertyValue())
            if (event.getPacket() instanceof C0BPacketEntityAction) {
                final C0BPacketEntityAction c0B = (C0BPacketEntityAction) event.getPacket();

                if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket
                            (new C0BPacketEntityAction(Minecraft.getMinecraft().thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING), null);
                    event.setCancelled(true);
                }

                if (c0B.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                    event.setCancelled(true);
                }
            }

        if (redesky.getPropertyValue()) {
            if (event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction
                    || event.getPacket() instanceof C13PacketPlayerAbilities || event.getPacket() instanceof C17PacketCustomPayload || event.getPacket() instanceof C18PacketSpectate)
                event.setCancelled(true);
        }

        if (hypixel.getPropertyValue()) {
            if (event.getPacket() instanceof S07PacketRespawn) {
                hasDisable = false;
                timer.reset();
            }

            if (event.getPacket() instanceof S08PacketPlayerPosLook
                    && !Main.INSTANCE.moduleManager.getModule(NoRotate.class).getState()) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    double d0 = ((S08PacketPlayerPosLook) event.getPacket()).getX();
                    double d1 = ((S08PacketPlayerPosLook) event.getPacket()).getY();
                    double d2 = ((S08PacketPlayerPosLook) event.getPacket()).getZ();
                    float f = ((S08PacketPlayerPosLook) event.getPacket()).getYaw();
                    float f1 = ((S08PacketPlayerPosLook) event.getPacket()).getPitch();

                    if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
                        d0 += mc.thePlayer.posX;
                    } else {
                        mc.thePlayer.motionX = 0.0D;
                    }

                    if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
                        d1 += mc.thePlayer.posY;
                    } else {
                        mc.thePlayer.motionY = 0.0D;
                    }

                    if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
                        d2 += mc.thePlayer.posZ;
                    } else {
                        mc.thePlayer.motionZ = 0.0D;
                    }

                    x = ((S08PacketPlayerPosLook) event.getPacket()).getX();
                    y = ((S08PacketPlayerPosLook) event.getPacket()).getY();
                    z = ((S08PacketPlayerPosLook) event.getPacket()).getZ();

                    mc.thePlayer.setPositionAndRotation(d0, d1, d2, f, f1);

                    if (freezeTimer.hasReached(10000) || (!resetTimer.hasReached(175)) && (updateFreeze.getPropertyValue()) && hasDisable)
                        freezeTimer.reset();

                    if (packetMeme.getPropertyValue())
                        if (!resetTimer.hasReached(175)) {
                            ChatUtils.info("Packet sent");
                            resetTimer.reset();
                            if (packetMemeEdit.getPropertyValue())
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(), ((S08PacketPlayerPosLook) event.getPacket()).getZ(), false));
                            else
                                event.setCancelled(true);

                            flagStock.add(new PosLookPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                    ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false)));
                            return;
                        } else {
                            for (PosLookPacket packet : flagStock) {
                                if (packet.isExpired()) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(packet.getPacket());
                                    flagStock.remove(packet);
                                }

                            }
                        }

                    if (!hasDisable)
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[0], playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[1],
                                playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[2], true));
                    else
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false));

                    resetTimer.reset();
                    event.setCancelled(true);
                }
            }

            if (event.getPacket() instanceof S00PacketKeepAlive) {
                if (hasDisable) {
                    if (packetBrust.getPropertyValue() || hypixel.getPropertyValue()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                    }
                }
            }

            if (event.getPacket() instanceof S32PacketConfirmTransaction) {
                if (((S32PacketConfirmTransaction) event.getPacket()).getWindowId() == 0
                        && ((S32PacketConfirmTransaction) event.getPacket()).getActionNumber() < 0) {
                    if (!hasDisable) {
                        hasDisable = true;
                        burst.reset();
                        invalid = 0;
                    }

                    if (packetBrust.getPropertyValue() || hypixel.getPropertyValue()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            if (lesspacket.getPropertyValue())
                if (!((C03PacketPlayer) event.getPacket()).isMoving() && !((C03PacketPlayer) event.getPacket()).getRotating())
                    event.setCancelled(true);

            if (!event.isCancelled()) {
                //Packet Dormant
                if (packetDormant.getPropertyValue() && mc.thePlayer.ticksExisted > 32) {
                    //Collect Packets for 1 second
                    if (!choke.hasReached(10000)) return;

                    if (!positionSpoof.getPropertyValue() || !(((C03PacketPlayer) event.getPacket()).isMoving() && ((C03PacketPlayer) event.getPacket()).getRotating()))
                        dormant.add(event.getPacket());
                    else
                        dormant.add(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ((C03PacketPlayer) event.getPacket()).isOnGround()));

                    event.setCancelled(true);
                    if (choke.hasReached(10000 + collectTimer.getPropertyValue().longValue() * 100)) choke.reset();
                }


                if (packetFreeze.getPropertyValue())
                    if (!freezeTimer.hasReached(freezeLatency.getPropertyValue().longValue() * 100)) {
                        dormant.add(event.getPacket());
                        event.setCancelled(true);
                    }

                //position Spoof
                if (!event.isCancelled()) {
                    if (positionSpoof.getPropertyValue())
                        if (((C03PacketPlayer) event.getPacket()).isMoving() && ((C03PacketPlayer) event.getPacket()).getRotating())
                            if (!packetDormant.getPropertyValue()) {
                                mc.getNetHandler().getNetworkManager().sendPacket
                                        (new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ((C03PacketPlayer) event.getPacket()).isOnGround()), null);
                                event.setCancelled(true);
                            }
                }
            }
        }
    };

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (mc.isSingleplayer()) return;

        if (packetBrust.getPropertyValue() || hypixel.getPropertyValue()) {
            if (!burst.hasReached(delay)) return;
            resetPackets(mc.getNetHandler());
            burst.reset();
        }

        if (packetDormant.getPropertyValue()) {
            if (!dormantTimer.hasReached(dormantDelay)) return;
            dormantTimer.reset();
            resetPacket();
            if (dormantDelay > dormantLatency.getPropertyValue() * 100) dormantDelay = 50;
            else dormantDelay += 25;
        }

        if (packetFreeze.getPropertyValue()) {
            if (!freezeTimer.hasReached(freezeLatency.getPropertyValue() * 100)) return;
            resetPacket();
        }

        if (!flagStock.isEmpty() && hasDisable) {
            for (PosLookPacket packet : Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock) {
                if (System.currentTimeMillis() - packet.time > 3000) {
                    mc.getNetHandler().getNetworkManager().sendPacket(packet.getPacket());
                    flagStock.remove(packet);
                }
            }
        }
    };


    private final IEventListener<MotionUpdateEvent> onMotionUpdate = event ->
    {
        if (mc.isSingleplayer()) return;
        if (event.getEventType() == EventType.PRE) {
            if (hypixel.getPropertyValue()) {
                if (!hasDisable && timer.hasReached(1000)) {
                    event.setX(0);
                    event.setY(120);
                    event.setZ(0);
                    event.setOnGround(true);
                }
            }
        }
    };

    private final IEventListener<UpdateEvent> onUpdate = event ->
    {
        if (mc.isSingleplayer()) return;

        if (ncp.getPropertyValue()) {
            if (mc.thePlayer.ticksExisted % 30 == 0) {
                mc.getNetHandler().getNetworkManager().sendPacket
                        (new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY
                                - (mc.thePlayer.onGround ? 0.1D : 1.1D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));

            }
        }

        if (fly.getPropertyValue()) {
            mc.getNetHandler().getNetworkManager().sendPacket
                    (new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255
                            , new ItemStack(Items.water_bucket), 0, 0.5f, 0));
            mc.getNetHandler().getNetworkManager().sendPacket
                    (new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1
                            , new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));

        }
    };


    //Reset Packets
    private void resetPackets(INetHandlerPlayClient netHandler) {
        if (packets.size() > 0) {
            synchronized (packets) {
                while (packets.size() != 0) {
                    packets.get(0).processPacket(netHandler);
                    if (packets.get(0) instanceof S32PacketConfirmTransaction) {
                        /*
                                                if (packetChoke.getPropertyValue()) {
                            if (invalid > 8) {
                                mc.getNetHandler().getNetworkManager().sendPacket(new C0FPacketConfirmTransaction(1, ((S32PacketConfirmTransaction) packets.get(0)).getActionNumber(), true));
                                invalid = 0;
                            }
                            invalid++;
                        }
                         */
                    }
                    packets.remove(packets.get(0));
                    if (delay > 375) delay = 200;
                    else delay += 25;
                }
            }
        }
    }

    //Reset Packets
    private void resetPacket() {
        if (dormant.size() > 0) {
            synchronized (dormant) {
                while (dormant.size() != 0) {
                    mc.getNetHandler().getNetworkManager().sendPacket(dormant.get(0), null);
                    dormant.remove(dormant.get(0));
                }
            }
        }
    }

    public static class PosLookPacket {
        private final C03PacketPlayer c03PacketPlayer;
        private final long time;

        public PosLookPacket(C03PacketPlayer c03PacketPlayer) {
            this.c03PacketPlayer = c03PacketPlayer;
            time = System.currentTimeMillis();
        }

        public C03PacketPlayer getPacket() {
            return c03PacketPlayer;
        }

        public long getTime() {
            return time;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - time > 1200;
        }
    }

}
