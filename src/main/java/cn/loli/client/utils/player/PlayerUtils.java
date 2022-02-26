package cn.loli.client.utils.player;

import cn.loli.client.utils.Utils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtils extends Utils {

    private static PlayerUtils utils;


    public static boolean isMoving() {
        return (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown());
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

    public static boolean isInLiquid() {
        if (mc.thePlayer.isInWater()) {
            return true;
        }
        boolean inLiquid = false;
        final int y = (int) mc.thePlayer.getEntityBoundingBox().minY;
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper
                    .floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && block.getMaterial() != Material.air) {
                    if (!(block instanceof BlockLiquid))
                        return false;
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }

    public static boolean isReallyOnGround() {
        Entity entity = Minecraft.getMinecraft().thePlayer;
        double y = entity.getEntityBoundingBox().offset(0.0D, -0.01D, 0.0D).minY;
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(entity.posX, y, entity.posZ)).getBlock();
        if (block != null && !(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return entity.onGround;
        }

        return false;
    }

    public static PlayerUtils getInstance() {
        if (utils == null) {
            utils = new PlayerUtils();
        }
        return utils;
    }
}
