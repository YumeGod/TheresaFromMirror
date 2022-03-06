package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.player.NoRotate;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.BlockPos;

public class Abuser extends Module {

    private final BooleanValue fly = new BooleanValue("Fly Bypass", false);
    private final BooleanValue range = new BooleanValue("Reach Bypass", false);
    private final BooleanValue ncp = new BooleanValue("NCP Flag", false);
    private final BooleanValue redesky = new BooleanValue("Rede Sky", false);
    private final BooleanValue hypixel = new BooleanValue("Hypixel-Semi", false);

    public boolean hasDisable;

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
            if (event.getPacket() instanceof S07PacketRespawn)
                hasDisable = false;

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

                    if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
                        f1 += mc.thePlayer.rotationPitch;
                    }

                    if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
                        f += mc.thePlayer.rotationYaw;
                    }

                    mc.thePlayer.setPositionAndRotation(d0, d1, d2,
                            mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

                    if (hasDisable)
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                ((S08PacketPlayerPosLook) event.getPacket()).getZ(), false));
                    else
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false));

                    event.setCancelled(true);
                }
            }

            if (event.getPacket() instanceof S32PacketConfirmTransaction) {
                if (((S32PacketConfirmTransaction) event.getPacket()).getWindowId() == 0
                        && ((S32PacketConfirmTransaction) event.getPacket()).getActionNumber() < 0)
                    if (mc.thePlayer.ticksExisted > 30 && !hasDisable)
                        hasDisable = true;
            }
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        if (hypixel.getObject()) {
            if (mc.currentScreen instanceof GuiDownloadTerrain && (mc.thePlayer != null) && !hasDisable)
                mc.thePlayer.closeScreen();
        }
    }

    @EventTarget
    private void onMotionUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            if (hypixel.getObject()) {
                if (!hasDisable) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(0, 0, 0, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                    event.setX(1993634.696969696969);
                    event.setY(1993634.696969696969);
                    event.setZ(1993634.696969696969);
                    event.setOnGround(false);
                }
            }
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
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

    //来点色图


}
