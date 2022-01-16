package cn.loli.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

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

    public static boolean isOnSameTeam(Entity entity) {
        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
                    || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            return Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2)
                    .equals(entity.getDisplayName().getUnformattedText().substring(0, 2));
        }
        return false;
    }

}
