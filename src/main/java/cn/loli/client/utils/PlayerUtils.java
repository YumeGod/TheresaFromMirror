package cn.loli.client.utils;

import net.minecraft.client.Minecraft;

public class PlayerUtils {
    private static final Minecraft mc;

    static {
        mc = Minecraft.getMinecraft();
    }

    public static boolean isMoving2() {
        return ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F));
    }

}
