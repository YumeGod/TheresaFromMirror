package cn.loli.client.module.modules.misc.skyblock;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.RenderBlockEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.rotation.RotationHook;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.block.*;
import net.minecraft.init.Items;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoFarm extends Module {

    final List<PlantSeedTask> plantSeedTaskArrayList = new ArrayList<>();
    float yaw, pitch;
    TimeHelper time = new TimeHelper();

    private final BooleanValue heuristic = new BooleanValue("Heuristic", false);
    private final BooleanValue slient = new BooleanValue("Slient-Rotate", false);

    PlantSeedTask action;

    public AutoFarm() {
        super("Auto Farm", "Auto do the farm work", ModuleCategory.MISC);
    }

    //TODO : Auto Farm

    @EventTarget
    public void onMotion(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE) {
            if (slient.getObject()) {
                e.setYaw(yaw);
                e.setPitch(pitch);
            }
        }
    }

    @EventTarget
    private void onRotate(RenderEvent e) {
        if (!heuristic.getObject()) {
            if (time.hasReached(500)) {
                for (double y = mc.thePlayer.posY + 12.0; y > mc.thePlayer.posY - 12.0; y -= 1.0) {
                    for (double x = mc.thePlayer.posX - 12.0; x < mc.thePlayer.posX + 12.0; x += 1.0) {
                        for (double z = mc.thePlayer.posZ - 12.0; z < mc.thePlayer.posZ + 12.0; z += 1.0) {
                            BlockPos pos = new BlockPos(x, y, z);

                            if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, false)) && isBlockValid(pos, false))
                                plantSeedTaskArrayList.add(new PlantSeedTask(pos, false));

                            if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, true)) && isBlockValid(pos, true))
                                plantSeedTaskArrayList.add(new PlantSeedTask(pos, true));
                        }
                    }
                }
                time.reset();
            }
        }

        plantSeedTaskArrayList.sort(Comparator.comparingDouble(o -> (mc.thePlayer.getDistance(o.pos.getX(), o.pos.getY(), o.pos.getZ()))));
        action = plantSeedTaskArrayList.get(0);

        if (action.pos != null) {
            float[] rot = rotationUtils.faceBlock(action.pos, mc.theWorld.getBlockState(action.pos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(action.pos).getBlock().getBlockBoundsMinY() + 0.5D,
                    false, false, false, false, false, false, 180);
            yaw = rot[0];
            pitch = rot[1];
        } else {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }
    }

    @EventTarget
    private void onSort(TickEvent e) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) return;

        if (action.pos != null) {
            if (mc.thePlayer.getDistance(action.pos.getX(), action.pos.getY(), action.pos.getZ()) > 1)
                moveUtils.addMotion(0.02, RotationHook.yaw);
        }
        if (!slient.getObject()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (action.pos != null) {
            if (isBlockValid(action.pos, action.isPlant)) {
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), action.pos, getFacingDirectionToPosition(action.pos), new Vec3(action.pos.getX(), action.pos.getY(), action.pos.getZ()))) {
                    mc.thePlayer.swingItem();
                }
            } else if (isBlockValid(action.pos, !action.isPlant)) {
                if (mc.playerController.clickBlock(action.pos, getFacingDirectionToPosition(action.pos))) {
                    mc.thePlayer.swingItem();
                }
            } else {
                action = null;
            }

            plantSeedTaskArrayList.remove(action);
        }
    }

    @EventTarget
    private void onRender(RenderBlockEvent e) {
        //Added to List
        if (heuristic.getObject()) {
            BlockPos pos = new BlockPos(e.x, e.y, e.z);
            if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, false)) && isBlockValid(pos, false))
                plantSeedTaskArrayList.add(new PlantSeedTask(pos, false));

            if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, true)) && isBlockValid(pos, true))
                plantSeedTaskArrayList.add(new PlantSeedTask(pos, true));
        }
    }

    private boolean isBlockValid(BlockPos position, boolean isPlant) {
        boolean valid = false;
        Block target = mc.theWorld.getBlockState(position).getBlock();
        if (isPlant) {
            if (target instanceof BlockFarmland) {
                BlockFarmland farmland = (BlockFarmland) target;
                if (!(mc.theWorld.getBlockState(position.offset(EnumFacing.UP)).getBlock() instanceof BlockCrops) && farmland.getMetaFromState(mc.theWorld.getBlockState(position)) == 7) {
                    valid = true;
                }
            } else if (target instanceof BlockSoulSand) {
                if (!(mc.theWorld.getBlockState(position.offset(EnumFacing.UP)).getBlock() instanceof BlockNetherWart)) {
                    valid = true;
                }
            } else if ((target instanceof BlockDirt || target instanceof BlockGrass || target instanceof BlockSand) && !(mc.theWorld.getBlockState(position.offset(EnumFacing.UP)).getBlock() instanceof BlockCrops) && !(mc.theWorld.getBlockState(position.offset(EnumFacing.UP)).getBlock() instanceof BlockReed) && mc.theWorld.isAnyLiquid(target.getCollisionBoundingBox(mc.theWorld, position, mc.theWorld.getBlockState(position)))) {
                valid = true;
            }
        } else {
            if (target instanceof BlockCrops) {
                BlockCrops crops = (BlockCrops) target;
                valid = crops.getMetaFromState(mc.theWorld.getBlockState(position)) == 7;
            } else if (target instanceof BlockNetherWart) {
                BlockNetherWart wart = (BlockNetherWart) target;
                valid = wart.getMetaFromState(mc.theWorld.getBlockState(position)) == 3;
            } else if (target instanceof BlockReed) {
                valid = mc.theWorld.getBlockState(position.offset(EnumFacing.DOWN)).getBlock() instanceof BlockReed;
            }
        }
        return valid && getFacingDirectionToPosition(position) != null && mc.thePlayer.getDistance(position.getX(), position.getY(), position.getZ()) < 24.0;
    }

    private boolean isStackValid(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Block block = mc.theWorld.getBlockState(this.action.pos).getBlock();
        return block instanceof BlockFarmland ? stack.getItem() instanceof ItemSeeds || stack.getItem() == Items.carrot || stack.getItem() == Items.potato : (block instanceof BlockSoulSand ? stack.getItem() == Items.nether_wart : (block instanceof BlockDirt || block instanceof BlockGrass || block instanceof BlockSand) && mc.theWorld.isAnyLiquid(block.getCollisionBoundingBox(mc.theWorld, this.action.pos, mc.theWorld.getBlockState(this.action.pos))) && stack.getItem() instanceof ItemReed);
    }

    private EnumFacing getFacingDirectionToPosition(BlockPos position) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(position.add(0, 1, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(position.add(0, -1, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(position.add(1, 0, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(position.add(-1, 0, 0)).getBlock().isFullCube()) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isFullCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isFullCube()) {
            direction = EnumFacing.NORTH;
        }
        return direction;
    }

    static class PlantSeedTask {
        private final BlockPos pos;
        private final boolean isPlant;

        public PlantSeedTask(BlockPos pos, final boolean isPlant) {
            this.pos = pos;
            this.isPlant = isPlant;
        }
    }

}
