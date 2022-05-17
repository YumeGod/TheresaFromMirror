package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.player.PlayerUtils;

import dev.xix.event.bus.IEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

public class DamnBridge extends Module {

    private float startYaw;
    private float startPitch;
    private BlockPos lastBlock;


    public DamnBridge() {
        super("Damn Bridge", "Godlike Bridge", ModuleCategory.PLAYER);
    }


    @Override
    public void onEnable() {
        
        try {
            if (mc.inGameHasFocus) {
                if (!playerUtils.isOnGround(-1))
                    this.setState(false);
                startYaw = mc.thePlayer.rotationYaw;
                startPitch = mc.thePlayer.rotationPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
                lastBlock = (isAirBlock(getBlock(new BlockPos(mc.thePlayer).down())) ? null : new BlockPos((Entity) mc.thePlayer).down());
            }
        } catch (Exception e) {
            ChatUtils.send(("Some Error"));
        }
    }


    @Override
    public void onDisable() {
        
        try {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        } catch (Exception e) {
            ChatUtils.send(("Some Error"));
        }
    }

    private final IEventListener<TickEvent> onTick = event ->
    {
        if (lastBlock == null || mc.playerController.getCurrentGameType().isCreative()) {
            ChatUtils.send(("Error: no blocks nearby found"));
            this.setState(false);
            return;
        }

        if (mc.thePlayer.posY == Math.round(mc.thePlayer.posY)) {
            mc.thePlayer.rotationYaw = startYaw;
            mc.thePlayer.rotationPitch = startPitch;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        }
        if (placeBlockSimple(new BlockPos(mc.thePlayer).up(2), true, 1.0f)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        } else if (placeBlockSimple(new BlockPos(mc.thePlayer).down(), true, 1.0f)) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
        } else {
            mc.thePlayer.rotationYaw = startYaw;
            mc.thePlayer.rotationPitch = startPitch;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        }
    };


    private boolean isAirBlock(final Block block) {
        return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125);
    }

    private boolean placeBlockSimple(final BlockPos pos, final boolean place, final float partialTicks) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!doesSlotHaveBlocks(mc.thePlayer.inventory.currentItem)) {
            mc.thePlayer.inventory.currentItem = getFirstHotBarSlotWithBlocks();
        }
        if (!isAirBlock(getBlock(pos))) {
            return false;
        }
        final Entity entity = mc.getRenderViewEntity();
        final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final Vec3 eyesPos = new Vec3(d0, d2 + mc.thePlayer.getEyeHeight(), d3);
        for (final EnumFacing side : EnumFacing.values()) {
            if (!side.equals(EnumFacing.UP)) {
                if (!side.equals(EnumFacing.DOWN) || Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                    final BlockPos neighbor = pos.offset(side);
                    final EnumFacing side2 = side.getOpposite();
                    if (getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        final Vec3 hitVec = new Vec3((Vec3i) neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()));
                        if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                            final float[] angles = getRotations(neighbor, side2, partialTicks);
                            if (place) {
                                mc.thePlayer.rotationYaw = angles[0];
                                mc.thePlayer.rotationPitch = angles[1];
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                                lastBlock = pos;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean doesSlotHaveBlocks(final int slotToCheck) {
        return mc.thePlayer.inventory.getStackInSlot(slotToCheck) != null && mc.thePlayer.inventory.getStackInSlot(slotToCheck).getItem() instanceof ItemBlock && mc.thePlayer.inventory.getStackInSlot(slotToCheck).stackSize > 0;
    }

    private Block getBlock(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    private int getFirstHotBarSlotWithBlocks() {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                return i;
            }
        }
        return 0;
    }

    private float[] getRotations(final BlockPos block, final EnumFacing face, final float partialTicks) {
        final Entity entity = mc.getRenderViewEntity();
        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final double x = block.getX() + mc.thePlayer.posX - Math.floor(mc.thePlayer.posX) - posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + mc.thePlayer.posZ - Math.floor(mc.thePlayer.posZ) - posZ + face.getFrontOffsetZ() / 2.0;
        final double y = block.getY() + 0.5;
        final double d1 = posY + mc.thePlayer.getEyeHeight() - y;
        final double d2 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float) (Math.atan2(d1, d2) * 180.0 / 3.141592653589793);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }
}
