package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.Render3DEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.ChatUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TellyBridge extends Module {

    public TellyBridge() {
        super("Telly Bridge", "Telly Telly~", ModuleCategory.PLAYER);
        phase = Phase.PreJump;
    }

    private final NumberProperty<Integer> distance = new NumberProperty<>("Distance", 5, 4, 6 , 1);

    private Phase phase;
    private float startYaw;
    private float startPitch;
    private BlockPos lastBlock;


    @Override
    public void onEnable() {
        try {
            if (mc.inGameHasFocus) {
                startYaw = mc.thePlayer.rotationYaw;
                startPitch = mc.thePlayer.rotationPitch;
                phase = Phase.PreJump;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                lastBlock = (isAirBlock(getBlock(new BlockPos(mc.thePlayer).down())) ? null : new BlockPos(mc.thePlayer).down());
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

    private final IEventListener<Render3DEvent> onRender = e ->
    {
        if (phase.equals(Phase.Placing))
            placeBlock((int) mc.playerController.getBlockReachDistance(), false, e.getPartialTicks());

    };

    private final IEventListener<TickEvent> onTick = e ->
    {
        if (lastBlock == null || mc.playerController.getCurrentGameType().isCreative()) {
            ChatUtils.send(("Some Error"));
            setState(false);
            return;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        if (Math.sqrt(mc.thePlayer.getDistanceSq(lastBlock)) > (distance.getPropertyValue() / 2)) {
            phase = Phase.Placing;
        } else if (!isAirBlock(getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ))) && phase.equals(Phase.Placing)) {
            phase = Phase.Turn;
        } else if (mc.thePlayer.isCollidedVertically && phase.equals(Phase.Turn)) {
            phase = Phase.Jump;
        }
        switch (phase) {
            case Turn: {
                mc.thePlayer.rotationYaw = startYaw;
                mc.thePlayer.rotationPitch = startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
            }
            case PreJump: {
                mc.thePlayer.rotationYaw = startYaw;
                mc.thePlayer.rotationPitch = startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
                phase = Phase.Jump;
                break;
            }
            case Jump: {
                mc.thePlayer.rotationYaw = startYaw;
                mc.thePlayer.rotationPitch = startPitch;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
                phase = Phase.Jumping;
                break;
            }
            case Jumping: {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                break;
            }
            case Placing: {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
                placeBlock((int) mc.playerController.getBlockReachDistance(), true, 0.0f);
                break;
            }
        }
    };


    private void placeBlock(final int range, final boolean place, final float partialTicks) {
        if (!isAirBlock(getBlock(new BlockPos(mc.thePlayer).down()))) {
            return;
        }
        if (placeBlockSimple(new BlockPos(mc.thePlayer).down(), place, partialTicks)) {
            if (place) {
                lastBlock = new BlockPos(mc.thePlayer).down();
            }
            return;
        }
        int dist = 0;
        while (dist <= range) {
            for (int blockDist = 0; dist != blockDist; ++blockDist) {
                for (int x = blockDist; x >= 0; --x) {
                    final int z = blockDist - x;
                    final int y = dist - blockDist;
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(z), place, partialTicks)) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(x).west(-z), place, partialTicks)) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(z), place, partialTicks)) {
                        return;
                    }
                    if (placeBlockSimple(new BlockPos(mc.thePlayer).down(y).north(-x).west(-z), place, partialTicks)) {
                        return;
                    }
                }
            }
            ++dist;
        }
    }

    private boolean isAirBlock(final Block block) {
        return block.getMaterial().isReplaceable() && (!(block instanceof BlockSnow) || block.getBlockBoundsMaxY() <= 0.125);
    }

    private boolean placeBlockSimple(final BlockPos pos, final boolean place, final float partialTicks) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Entity entity = mc.getRenderViewEntity();
        final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final Vec3 eyesPos = new Vec3(d0, d2 + mc.thePlayer.getEyeHeight(), d3);
        for (final EnumFacing side : EnumFacing.values()) {
            if (!side.equals(EnumFacing.UP)) {
                if (!side.equals(EnumFacing.DOWN)) {
                    final BlockPos neighbor = pos.offset(side);
                    final EnumFacing side2 = side.getOpposite();
                    if (getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        final Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()));
                        if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                            final float[] angles = getRotations(neighbor, side2, partialTicks);
                            mc.getRenderViewEntity().rotationYaw = angles[0];
                            mc.getRenderViewEntity().rotationPitch = angles[1];
                            if (place) {
                                mc.thePlayer.rotationYaw = angles[0];
                                mc.thePlayer.rotationPitch = angles[1];
                                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), neighbor, side2, hitVec);
                                mc.thePlayer.swingItem();
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

    private static Block getBlock(final BlockPos pos) {
        return Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
    }

    private float[] getRotations(final BlockPos block, final EnumFacing face, final float partialTicks) {
        final Entity entity = mc.getRenderViewEntity();
        final double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        final double x = block.getX() + 0.5 - posX + face.getFrontOffsetX() / 2.0;
        final double z = block.getZ() + 0.5 - posZ + face.getFrontOffsetZ() / 2.0;
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

    enum Phase {
        PreJump,
        Turn,
        Jump,
        Jumping,
        Placing;
    }
}
