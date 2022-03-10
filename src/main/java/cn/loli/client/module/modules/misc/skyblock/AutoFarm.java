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
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemReed;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoFarm extends Module {

    final List<PlantSeedTask> plantSeedTaskArrayList = new ArrayList<>();
    float yaw, pitch;
    TimeHelper time = new TimeHelper();
    TimeHelper delay = new TimeHelper();
    MovingObjectPosition ray;

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
                for (int y = 6; y >= -1; --y)
                    for (int x = -9; x <= 9; ++x)
                        for (int z = -9; z <= 9; ++z)
                            if (x != 0 || z != 0) {
                                BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);

                                if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, true)) && isBlockValid(pos, true))
                                    plantSeedTaskArrayList.add(new PlantSeedTask(pos, true));

                                if (!plantSeedTaskArrayList.contains(new PlantSeedTask(pos, false)) && isBlockValid(pos, false))
                                    plantSeedTaskArrayList.add(new PlantSeedTask(pos, false));

                            }

                time.reset();
            }
        }

        plantSeedTaskArrayList.removeIf(task -> !isBlockValid(task.pos, task.isPlant));

        plantSeedTaskArrayList.sort
                (Comparator.comparingDouble(o -> (mc.thePlayer.getDistance(o.pos.getX(), o.pos.getY(), o.pos.getZ()))));

        if ((action == null || !plantSeedTaskArrayList.contains(action))
                && !plantSeedTaskArrayList.isEmpty())
            action = plantSeedTaskArrayList.get(0);

        if (action != null) {
            if (action.pos != null) {
                float[] rot = rotationUtils.faceBlock(action.pos, 0.0
                        , false, false, false, true, false, false, 10);
                yaw = rot[0];
                pitch = rot[1];
                ray = rotationUtils.rayCastedBlock(RotationHook.yaw, RotationHook.pitch);
            }
        } else {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }
    }

    @EventTarget
    private void onSort(TickEvent e) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) return;

        if (!slient.getObject()) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (action.pos != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), mc.thePlayer.isCollidedHorizontally);

            if (mc.thePlayer.getDistance(action.pos.getX(), action.pos.getY(), action.pos.getZ()) > 1)
                moveUtils.setSpeed(0.22, RotationHook.yaw);
        }

        if (ray.sideHit != null) {
            if (ray.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return;

            if (action.isPlant) {
                if (!isBlockValid(ray.getBlockPos(), true))
                    return;

                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(),
                        ray.getBlockPos(), ray.sideHit, ray.hitVec)) {
                    mc.thePlayer.swingItem();
                    plantSeedTaskArrayList.remove(action);
                    action = null;

                    delay.reset();
                }
            } else {
                if (!isBlockValid(ray.getBlockPos(), false))
                    return;

                if (mc.playerController.onPlayerDamageBlock(ray.getBlockPos(), ray.sideHit)) {
                    mc.thePlayer.swingItem();
                    plantSeedTaskArrayList.remove(action);
                    action = null;

                    delay.reset();
                }

            }
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
        boolean temp = false;
        Block block = mc.theWorld.getBlockState(position).getBlock();
        if (isPlant) {
            if (mc.thePlayer.getHeldItem().getItem() == Items.nether_wart) {
                if (block instanceof BlockSoulSand) {
                    if (mc.theWorld.getBlockState(position.up()).getBlock() == Blocks.air) {
                        temp = true;
                    }
                }
            }
            if (mc.thePlayer.getHeldItem().getItem() == Items.reeds) {
                if (block instanceof BlockGrass || block instanceof BlockDirt || block instanceof BlockSand) {
                    if (mc.theWorld.getBlockState(position.up()).getBlock() == Blocks.air) {
                        for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
                            IBlockState blockState = mc.theWorld.getBlockState(position.offset(side));
                            if (blockState.getBlock().getMaterial() == Material.water || blockState.getBlock() == Blocks.ice) {
                                temp = true;
                            }
                        }
                    }
                }
            }
            if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSeeds || mc.thePlayer.getHeldItem().getItem() instanceof ItemSeedFood) {
                if (block instanceof BlockFarmland) {
                    if (mc.theWorld.getBlockState(position.up()).getBlock() == Blocks.air) {
                        temp = true;
                    }
                }
            }
        } else {
            if ((block instanceof BlockTallGrass) || (block instanceof BlockFlower) || (block instanceof BlockDoublePlant)) {
                temp = true;
            } else if (block instanceof BlockCrops) {
                BlockCrops crops = (BlockCrops) block;
                if (crops.getMetaFromState(mc.theWorld.getBlockState(position)) == 7) { // Crops are grown
                    temp = true;
                }
            } else if (block instanceof BlockNetherWart) {
                BlockNetherWart netherWart = (BlockNetherWart) block;
                if (netherWart.getMetaFromState(mc.theWorld.getBlockState(position)) == 3) { // Nether Wart is grown
                    temp = true;
                }
            } else if (block instanceof BlockReed) {
                if (mc.theWorld.getBlockState(position.down()).getBlock() instanceof BlockReed) { // Check if a reed is under it
                    temp = true;
                }
            } else if (block instanceof BlockCactus) {
                if (mc.theWorld.getBlockState(position.down()).getBlock() instanceof BlockCactus) { // Check if a cactus is under it
                    temp = true;
                }
            } else if (block instanceof BlockPumpkin) {
                temp = true;
            } else if (block instanceof BlockMelon) {
                temp = true;
            }
        }

        return temp && mc.thePlayer.getDistance(position.getX(), position.getY(), position.getZ()) <= 18;
    }
    
    /*
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
    */

    private boolean isStackValid(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Block block = mc.theWorld.getBlockState(this.action.pos).getBlock();
        return block instanceof BlockFarmland ? stack.getItem() instanceof ItemSeeds || stack.getItem() == Items.carrot || stack.getItem() == Items.potato : (block instanceof BlockSoulSand ? stack.getItem() == Items.nether_wart : (block instanceof BlockDirt || block instanceof BlockGrass || block instanceof BlockSand) && mc.theWorld.isAnyLiquid(block.getCollisionBoundingBox(mc.theWorld, this.action.pos, mc.theWorld.getBlockState(this.action.pos))) && stack.getItem() instanceof ItemReed);
    }

    private EnumFacing getFacingDirectionToPosition(BlockPos position) {
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(position.add(0, 1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.UP;
        } else if (!mc.theWorld.getBlockState(position.add(0, -1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.DOWN;
        } else if (!mc.theWorld.getBlockState(position.add(1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.EAST;
        } else if (!mc.theWorld.getBlockState(position.add(-1, 0, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.WEST;
        } else if (!mc.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.SOUTH;
        } else if (!mc.theWorld.getBlockState(position.add(0, 0, 1)).getBlock().isBlockNormalCube()) {
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
