package cn.loli.client.module.modules.player;

import cn.loli.client.Main;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.Abuser;
import cn.loli.client.utils.misc.ChatUtils;

import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("NoRotate", "Just desync the rotation from serverside", ModuleCategory.PLAYER);
    }

    private final IEventListener<PacketEvent> onPacket = event ->
    {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (mc.thePlayer.rotationPitch == 0.0F
                        || mc.isSingleplayer())
                    return;

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

                Main.INSTANCE.moduleManager.getModule(Abuser.class).x = ((S08PacketPlayerPosLook) event.getPacket()).getX();
                Main.INSTANCE.moduleManager.getModule(Abuser.class).y = ((S08PacketPlayerPosLook) event.getPacket()).getY();
                Main.INSTANCE.moduleManager.getModule(Abuser.class).z = ((S08PacketPlayerPosLook) event.getPacket()).getZ();

                mc.thePlayer.setPositionAndRotation(d0, d1, d2,
                        mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

                if (Main.INSTANCE.moduleManager.getModule(Abuser.class).getState()) {
                    if (Main.INSTANCE.moduleManager.getModule(Abuser.class).freezeTimer.hasReached(10000) ||
                            (!Main.INSTANCE.moduleManager.getModule(Abuser.class).resetTimer.hasReached(175) && Main.INSTANCE.moduleManager.getModule(Abuser.class).updateFreeze.getPropertyValue() && Main.INSTANCE.moduleManager.getModule(Abuser.class).hasDisable))
                        Main.INSTANCE.moduleManager.getModule(Abuser.class).freezeTimer.reset();

                    if (Main.INSTANCE.moduleManager.getModule(Abuser.class).packetMeme.getPropertyValue())
                        if (!Main.INSTANCE.moduleManager.getModule(Abuser.class).resetTimer.hasReached(175)) {
                            ChatUtils.info("Packet sent");
                            Main.INSTANCE.moduleManager.getModule(Abuser.class).resetTimer.reset();
                            if (Main.INSTANCE.moduleManager.getModule(Abuser.class).packetMemeEdit.getPropertyValue())
                                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY() + 0.01,
                                        ((S08PacketPlayerPosLook) event.getPacket()).getZ(), false));
                            else
                                event.setCancelled(true);

                            Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock.add(new Abuser.PosLookPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                                    ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false)));
                            return;
                        } else {
                            for (Abuser.PosLookPacket packet : Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock) {
                                if (packet.isExpired()) {
                                    mc.getNetHandler().getNetworkManager().sendPacket(packet.getPacket());
                                    Main.INSTANCE.moduleManager.getModule(Abuser.class).flagStock.remove(packet);
                                }
                            }
                        }

                }


                if (!Main.INSTANCE.moduleManager.getModule(Abuser.class).hasDisable && Main.INSTANCE.moduleManager.getModule(Abuser.class).getState())
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[0], playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[1],
                            playerUtils.randomInRange(-0.99, 0.99) + Main.INSTANCE.pos[2], true));
                else
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                            ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false));

                Main.INSTANCE.moduleManager.getModule(Abuser.class).resetTimer.reset();
                event.setCancelled(true);
            }
        }
    };



}
