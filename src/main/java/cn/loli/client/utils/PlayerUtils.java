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

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

}
