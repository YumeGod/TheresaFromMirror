package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.player.NoRotate;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.gui.GuiDownloadTerrain;
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

public class Abuser extends Module {

    private final BooleanValue fly = new BooleanValue("Fly Bypass", false);
    private final BooleanValue range = new BooleanValue("Reach Bypass", false);
    private final BooleanValue ncp = new BooleanValue("NCP Flag", false);
    private final BooleanValue redesky = new BooleanValue("Rede Sky", false);
    private final BooleanValue hypixel = new BooleanValue("Hypixel-Semi", false);
    private final BooleanValue packetChoke = new BooleanValue("Hypixel-Obfuscation", false);
    private final BooleanValue packetBrust = new BooleanValue("Brust", false);
    private final BooleanValue lesspacket = new BooleanValue("Less-Packet", false);

    public boolean hasDisable;
    public double x, y, z;
    public TimeHelper timer = new TimeHelper();
    private final TimeHelper brust = new TimeHelper();
    private final ArrayList<Packet<INetHandlerPlayClient>> packets = new ArrayList<>();
    long delay = 150;
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

    @EventTarget
    private void onPost(PacketEvent event) {
        if (mc.isSingleplayer()) return;

        if (range.getObject()) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
                if (((C0FPacketConfirmTransaction) event.getPacket()).getWindowId() >= 0)
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

                    mc.thePlayer.setPositionAndRotation(d0, d1, d2,
                            mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

                    if (!hasDisable && timer.hasReached(2000))
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(13371337.696969, 13371337.696969,
                                13371337.696969, true));
                    else
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false));

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
                        brust.reset();
                        invalid = 0;
                    }

                    if (packetBrust.getObject() || hypixel.getObject()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                    }
                }
            }


            if (event.getPacket() instanceof C03PacketPlayer)
                if (lesspacket.getObject())
                    if (!((C03PacketPlayer) event.getPacket()).isMoving() && !((C03PacketPlayer) event.getPacket()).getRotating())
                        event.setCancelled(true);


            if (event.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                if (hasDisable) {
                    if (x == ((C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket()).getPositionX() && y == ((C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket()).getPositionY()
                            && z == ((C03PacketPlayer.C06PacketPlayerPosLook) event.getPacket()).getPositionZ()) {
                    }
                }

            }
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        if (mc.isSingleplayer()) return;

        if (hypixel.getObject()) {
            if (mc.currentScreen instanceof GuiDownloadTerrain && (mc.thePlayer != null))
                mc.thePlayer.closeScreen();
        }

        if (packetBrust.getObject() || hypixel.getObject()) {
            if (!brust.hasReached(delay)) return;
            resetPackets(mc.getNetHandler());
            if (delay > 450) delay = 200;
            else delay += 25;
            brust.reset();
        }
    }

    @EventTarget
    private void onMotionUpdate(MotionUpdateEvent event) {
        if (mc.isSingleplayer()) return;
        if (event.getEventType() == EventType.PRE) {
            if (hypixel.getObject()) {
                if (!hasDisable && timer.hasReached(2000)) {
                    event.setX(1993634.696969696969);
                    event.setY(1993634.696969696969);
                    event.setZ(1993634.696969696969);
                    event.setOnGround(true);
                }
            }
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
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
    }


    //Reset Packets
    private void resetPackets(INetHandlerPlayClient netHandler) {
        if (packets.size() > 0) {
            synchronized (packets) {
                while (packets.size() != 0) {
                    try {
                        packets.get(0).processPacket(netHandler);
                        if (packets.get(0) instanceof S32PacketConfirmTransaction) {
                            if (packetChoke.getObject()) {
                                if (invalid > 8) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(new C0FPacketConfirmTransaction(1, ((S32PacketConfirmTransaction) packets.get(0)).getActionNumber(), true));
                                    invalid = 0;
                                    delay = 100;
                                }
                                invalid++;
                            }
                        }

                    } catch (Exception ignored) {
                    }
                    packets.remove(packets.get(0));
                }
            }
        }
    }

}
