package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.player.NoRotate;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
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

    private final BooleanValue fly = new BooleanValue("OldVerus-Fly-Bypass", false);
    private final BooleanValue range = new BooleanValue("Simple-Reach-Bypass", false);
    private final BooleanValue ncp = new BooleanValue("NCP-Timer-Flag", false);
    private final BooleanValue redesky = new BooleanValue("Rede-Sky-Semi", false);
    private final BooleanValue hypixel = new BooleanValue("Hypixel-Semi", false);
    public final BooleanValue packetMeme = new BooleanValue("Hypixel-Meme", false);
    public final BooleanValue packetMemeEdit = new BooleanValue("Hypixel-Meme-Transform", false);
    private final BooleanValue packetFreeze = new BooleanValue("Hypixel-Freeze", false);
    public final BooleanValue updateFreeze = new BooleanValue("Hypixel-Freeze-Update", true);
    private final BooleanValue packetBrust = new BooleanValue("Brust", false);
    private final BooleanValue lesspacket = new BooleanValue("Less-Packet", false);
    private final BooleanValue packetDormant = new BooleanValue("Position-Dormant", false);
    private final NumberValue<Integer> collectTimer = new NumberValue<>("Dormant-Collect-Timer", 10, 1, 30);
    private final NumberValue<Integer> dormantLatency = new NumberValue<>("Dormant-Collect-Latency", 4, 1, 20);
    private final NumberValue<Integer> freezeLatency = new NumberValue<>("Freeze-Collect-Latency", 15, 10, 50);
    private final BooleanValue positionSpoof = new BooleanValue("Position-Spoof", false);
    private final BooleanValue sneak = new BooleanValue("Sprint-Spoof", false);

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

        if (range.getObject()) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (((C0FPacketConfirmTransaction) event.getPacket()).getWindowId() >= 0)
                    event.setCancelled(true);
            }
        }

        if (sneak.getObject())
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

        if (redesky.getObject()) {
            if (event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction
                    || event.getPacket() instanceof C13PacketPlayerAbilities || event.getPacket() instanceof C17PacketCustomPayload || event.getPacket() instanceof C18PacketSpectate)
                event.setCancelled(true);
        }

        if (hypixel.getObject()) {
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

                    if (freezeTimer.hasReached(10000) || (!resetTimer.hasReached(175)) && (updateFreeze.getObject()) && hasDisable)
                        freezeTimer.reset();

                    if (packetMeme.getObject())
                        if (!resetTimer.hasReached(175)) {
                            ChatUtils.info("Packet sent");
                            resetTimer.reset();
                            if (packetMemeEdit.getObject())
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
                    if (packetBrust.getObject() || hypixel.getObject()) {
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

                    if (packetBrust.getObject() || hypixel.getObject()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                    }
                }
            }
        }

        if (event.getPacket() instanceof C03PacketPlayer) {
            if (lesspacket.getObject())
                if (!((C03PacketPlayer) event.getPacket()).isMoving() && !((C03PacketPlayer) event.getPacket()).getRotating())
                    event.setCancelled(true);

            if (!event.isCancelled()) {
                //Packet Dormant
                if (packetDormant.getObject() && mc.thePlayer.ticksExisted > 32) {
                    //Collect Packets for 1 second
                    if (!choke.hasReached(10000)) return;

                    if (!positionSpoof.getObject() || !(((C03PacketPlayer) event.getPacket()).isMoving() && ((C03PacketPlayer) event.getPacket()).getRotating()))
                        dormant.add(event.getPacket());
                    else
                        dormant.add(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ((C03PacketPlayer) event.getPacket()).isOnGround()));

                    event.setCancelled(true);
                    if (choke.hasReached(10000 + collectTimer.getObject().longValue() * 100)) choke.reset();
                }


                if (packetFreeze.getObject())
                    if (!freezeTimer.hasReached(freezeLatency.getObject().longValue() * 100)) {
                        dormant.add(event.getPacket());
                        event.setCancelled(true);
                    }

                //position Spoof
                if (!event.isCancelled()) {
                    if (positionSpoof.getObject())
                        if (((C03PacketPlayer) event.getPacket()).isMoving() && ((C03PacketPlayer) event.getPacket()).getRotating())
                            if (!packetDormant.getObject()) {
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

        if (packetBrust.getObject() || hypixel.getObject()) {
            if (!burst.hasReached(delay)) return;
            resetPackets(mc.getNetHandler());
            burst.reset();
        }

        if (packetDormant.getObject()) {
            if (!dormantTimer.hasReached(dormantDelay)) return;
            dormantTimer.reset();
            resetPacket();
            if (dormantDelay > dormantLatency.getObject() * 100) dormantDelay = 50;
            else dormantDelay += 25;
        }

        if (packetFreeze.getObject()) {
            if (!freezeTimer.hasReached(freezeLatency.getObject() * 100)) return;
            resetPacket();
        }

        if (!flagStock.isEmpty() && hasDisable) {
            for (PosLookPacket packet : Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock) {
                if (System.currentTimeMillis() - packet.time > 3000) {
                    mc.getNetHandler().getNetworkManager().sendPacket(packet.getPacket());
                    Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock.remove(packet);
                }
            }
        }
    };


    private final IEventListener<MotionUpdateEvent> onMotionUpdate = event ->
    {
        if (mc.isSingleplayer()) return;
        if (event.getEventType() == EventType.PRE) {
            if (hypixel.getObject()) {
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

        if (ncp.getObject()) {
            if (mc.thePlayer.ticksExisted % 30 == 0) {
                mc.getNetHandler().getNetworkManager().sendPacket
                        (new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY
                                - (mc.thePlayer.onGround ? 0.1D : 1.1D), mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));

            }
        }

        if (fly.getObject()) {
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
                                                if (packetChoke.getObject()) {
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
