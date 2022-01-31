package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

public class Abuser extends Module {

    private final BooleanValue fly = new BooleanValue("Fly Bypass", false);
    private final BooleanValue range = new BooleanValue("Reach Bypass", false);
    private final BooleanValue ncp = new BooleanValue("NCP Flag", false);
    private final BooleanValue redesky = new BooleanValue("Rede Sky", false);

    public Abuser() {
        super("Abuser", "Abuse Something which you can bypass some anti cheat", ModuleCategory.MISC);
    }


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
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

}
