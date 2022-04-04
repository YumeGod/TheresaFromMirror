package cn.loli.client.module.modules.player;

import cn.loli.client.Main;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.Abuser;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {

    public NoRotate() {
        super("NoRotate", "Just desync the rotation from serverside", ModuleCategory.PLAYER);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (!Main.INSTANCE.moduleManager.getModule(Abuser.class).getState() && mc.thePlayer.rotationPitch == 0.0F
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

                if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
                    f1 += mc.thePlayer.rotationPitch;
                }

                if (((S08PacketPlayerPosLook) event.getPacket()).func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
                    f += mc.thePlayer.rotationYaw;
                }

                Main.INSTANCE.moduleManager.getModule(Abuser.class).x = ((S08PacketPlayerPosLook) event.getPacket()).getX();
                Main.INSTANCE.moduleManager.getModule(Abuser.class).y = ((S08PacketPlayerPosLook) event.getPacket()).getY();
                Main.INSTANCE.moduleManager.getModule(Abuser.class).z = ((S08PacketPlayerPosLook) event.getPacket()).getZ();

                mc.thePlayer.setPositionAndRotation(d0, d1, d2,
                        mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);


                if (!Main.INSTANCE.moduleManager.getModule(Abuser.class).hasDisable && Main.INSTANCE.moduleManager.getModule(Abuser.class).getState())
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(13371337.696969, 13371337.696969,
                            13371337.696969, true));
                else
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(((S08PacketPlayerPosLook) event.getPacket()).getX(), ((S08PacketPlayerPosLook) event.getPacket()).getY(),
                            ((S08PacketPlayerPosLook) event.getPacket()).getZ(), ((S08PacketPlayerPosLook) event.getPacket()).getYaw(), ((S08PacketPlayerPosLook) event.getPacket()).getPitch(), false));

                if (Main.INSTANCE.moduleManager.getModule(Abuser.class).freezeTimer.hasReached(10000))
                    Main.INSTANCE.moduleManager.getModule(Abuser.class).freezeTimer.reset();

                event.setCancelled(true);
            }
        }
    }

}
