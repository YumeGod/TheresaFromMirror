package cn.loli.client.module.modules.player;

import cn.loli.client.events.*;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.MoveUtils;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 0, 0, 500);
    private final BooleanValue random = new BooleanValue("Delay-Randomize", false);

    private final BooleanValue slient = new BooleanValue("Slient", false);
    private final BooleanValue expand = new BooleanValue("Expend", false);
    private final BooleanValue diagonal = new BooleanValue("Diagonal", false);

    private final BooleanValue mouseFix = new BooleanValue("Mouse Fix", true);
    private final BooleanValue mouse_vl_fix = new BooleanValue("Mouse VL Fix", true);

    private final NumberValue<Integer> expandLength = new NumberValue<>("Expand Length", 0, 0, 6);
    private final BooleanValue allDirectionExpand = new BooleanValue("All Direction", true);

    private final BooleanValue rotation = new BooleanValue("Rotation", false);
    private final BooleanValue keeprotation = new BooleanValue("Keep Rotation", false);

    private final BooleanValue dynamicYaw = new BooleanValue("Dynamic Yaw", false);
    private final BooleanValue randomizePitch = new BooleanValue("Randomize Pitch", false);

    private final BooleanValue staticPitch = new BooleanValue("Static Pitch", false);
    private final NumberValue<Integer> pitch = new NumberValue<>("Static Pitch Range", 80, 70, 90);
    private final NumberValue<Integer> switchDelay = new NumberValue<>("Switch Delay", 0, 0, 1000);


    private final BooleanValue rayCast = new BooleanValue("Ray Cast", false);
    private final BooleanValue clampYaw = new BooleanValue("Clamp", true);

    private final BooleanValue moveFix = new BooleanValue("Move Fix", false);
    private final BooleanValue silentMoveFix = new BooleanValue("Silent Fix", false);
    private final BooleanValue shouldyaw = new BooleanValue("Silent Yaw Fix", false);

    private final BooleanValue upScaffold = new BooleanValue("UP-Scaffold", false);
    private final BooleanValue downScaffold = new BooleanValue("Down-Scaffold", false);

    private final BooleanValue sprint = new BooleanValue("Sprint", true);
    private final BooleanValue jump = new BooleanValue("AutoJump", false);
    private final BooleanValue sameY = new BooleanValue("Same Y", false);

    private final BooleanValue prediction = new BooleanValue("Prediction", false);
    private final BooleanValue randomAim = new BooleanValue("Random Aim", false);

    private final BooleanValue simple = new BooleanValue("Simple", false);

    private final BooleanValue rotateInAir = new BooleanValue("Rotation When Air", false);

    private final BooleanValue blockCheck = new BooleanValue("Check Block", false);
    private final BooleanValue allowAir = new BooleanValue("Allow Air", false);
    private final BooleanValue switchBlocks = new BooleanValue("Block Picker", false);
    private final BooleanValue automaticVector = new BooleanValue("Auto HitVec", false);

    private final BooleanValue facingCheck = new BooleanValue("Facing Check", false);
    private final BooleanValue canUpCheck = new BooleanValue("Can UP Check", false);

    private final BooleanValue noSwing = new BooleanValue("No Swing", false);


    private final BooleanValue mistake = new BooleanValue("Mistake", false);
    private final NumberValue<Integer> mistakerate = new NumberValue<>("Mistake Rate", 80, 70, 90);

    int silentSlot = -1;
    int startY;

    boolean canBuild, hasSilent;

    EnumFacing enumFacing;
    ItemStack itemStack;

    int currentBlocks;

    float curYaw, curPitch;
    final RotationUtils utils = RotationUtils.getInstance();

    private final List<Block> blackList;
    private final List<Integer> switchedSlots = new ArrayList<>();

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper switchTimer = new TimeHelper();
    private final TimeHelper itemSwitchTimer = new TimeHelper();

    BlockPos curPos;

    public Scaffold() {
        super("Scaffold", "Its place blocks under you", ModuleCategory.PLAYER);
        this.blackList =
                Arrays.asList(Blocks.waterlily, Blocks.heavy_weighted_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.red_flower, Blocks.yellow_flower, Blocks.crafting_table, Blocks.chest, Blocks.enchanting_table, Blocks.anvil, Blocks.sand, Blocks.gravel, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.ice, Blocks.packed_ice, Blocks.cobblestone_wall, Blocks.water, Blocks.lava, Blocks.web, Blocks.sapling, Blocks.rail, Blocks.golden_rail, Blocks.activator_rail, Blocks.detector_rail, Blocks.tnt, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot, Blocks.tallgrass, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.ladder, Blocks.torch, Blocks.stone_button, Blocks.wooden_button, Blocks.redstone_torch, Blocks.redstone_wire, Blocks.furnace, Blocks.cactus, Blocks.oak_fence, Blocks.acacia_fence, Blocks.nether_brick_fence, Blocks.birch_fence, Blocks.dark_oak_fence, Blocks.jungle_fence, Blocks.oak_fence, Blocks.acacia_fence_gate, Blocks.snow_layer, Blocks.trapdoor, Blocks.ender_chest, Blocks.trapped_chest, Blocks.beacon, Blocks.hopper, Blocks.daylight_detector, Blocks.daylight_detector_inverted, Blocks.carpet, Blocks.noteblock);
    }


    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null)
            return;

        hasSilent = false;
        canBuild = false;
        currentBlocks = 0;
        switchedSlots.clear();
        silentSlot = -1;
        startY = (int) mc.thePlayer.posY;

        curYaw = mc.thePlayer.rotationYaw;
        curPitch = mc.thePlayer.rotationPitch;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        switchTimer.reset();
    }

    @EventTarget
    private void onRender(RenderEvent e) {

        if (curPos != null) {
            if ((!mc.thePlayer.onGround && rotateInAir.getObject()) || (mc.objectMouseOver == null || (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || (!mc.objectMouseOver.getBlockPos().equals(curPos)) && mc.objectMouseOver.sideHit == enumFacing || (mc.objectMouseOver.sideHit != enumFacing && mc.objectMouseOver.getBlockPos().equals(curPos))))) {
                float[] rotation = utils.faceBlock(curPos, mc.theWorld.getBlockState(curPos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(curPos).getBlock().getBlockBoundsMinY() + 0.5D, mouse_vl_fix.getObject(), mouseFix.getObject(), prediction.getObject(), randomAim.getObject(), randomizePitch.getObject(), clampYaw.getObject(), 180);

                if (rotation != null)
                    if (simple.getObject()) {
                        curYaw = (dynamicYaw.getObject() ? MoveUtils.getDirection(mc.thePlayer.rotationYaw) : mc.thePlayer.rotationYaw) + 180;
                        curPitch = mc.thePlayer.onGround || staticPitch.getObject() ? (float) pitch.getObject() : rotation[1];
                    } else {
                        curYaw = rotation[0];
                        if (!staticPitch.getObject())
                            curPitch = rotation[1];
                        else
                            curPitch = (float) pitch.getObject();
                    }
            }
        }

    }

    @EventTarget
    private void onMoveFly(MoveFlyEvent e) {
        if (moveFix.getObject() && rotation.getObject())
            e.setYaw(curYaw);
    }


    @EventTarget
    private void onJump(JumpYawEvent e) {
        if (moveFix.getObject() && rotation.getObject())
            e.setYaw(curYaw);
    }


    @EventTarget
    private void onSlient(MovementStateEvent e) {
        if (moveFix.getObject() && rotation.getObject() && silentMoveFix.getObject()) {
            e.setSilentMoveFix(true);
            e.setYaw(curYaw);
            if (shouldyaw.getObject()) {
                e.setShouldYaw(utils.getYaw(calcShouldYaw()) + 180);
                e.setFixYaw(true);
            }
        }
    }


    @EventTarget
    private void onMotion(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE) {
            if (rotation.getObject()) {
                if (keeprotation.getObject() || utils.getBlockUnderPlayer(0.01F) == Blocks.air) {
                    e.setYaw(mc.thePlayer.rotationYawHead = curYaw);
                    e.setPitch(curPitch + (float) (randomizePitch.getObject() ? utils.randomInRange(-0.1, 0.1) : 0));
                }
            }
        }


        if (e.getEventType() == EventType.POST) {

        }
    }

    @EventTarget
    private void onWorking(AttackEvent e) {

        final double y = sameY.getObject() && PlayerUtils.isMoving2() ? startY : mc.thePlayer.posY;

        if (!sprint.getObject()) {
            ((IAccessorKeyBinding) mc.gameSettings.keyBindSprint).setPressed(false);
            mc.thePlayer.setSprinting(false);
        }

        if ((!PlayerUtils.isMoving2() && mc.gameSettings.keyBindJump.isKeyDown()) || downScaffold.getObject() && utils.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) || mc.thePlayer.onGround)
            startY = (int) mc.thePlayer.posY;

        boolean flag = true;


        Vec3 position = expand(mc.thePlayer.getPositionVector().addVector(0, (y - mc.thePlayer.posY), 0));
        this.curPos = getBlockPosToPlaceOn(new BlockPos(position.xCoord, position.yCoord - 1, position.zCoord));

        itemStack = mc.thePlayer.getCurrentEquippedItem();

        if (curPos != null) {
            if (!mc.thePlayer.isUsingItem())
                canBuild = true;

            if (blockCheck.getObject() && allowAir.getObject() || !rayCast.getObject()
                    || (mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null && mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air))
                if (!rayCast.getObject() || (mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null)) {
                    if (timeHelper.hasReached(delay.getObject())) {
                        final BlockPos blockpos = rayCast.getObject() ? mc.objectMouseOver.getBlockPos() : curPos;
                        if (blockpos != null && mc.theWorld.getBlockState(curPos) != null && mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            if (this.silentSlot != -1) {
                                final ItemStack item = mc.thePlayer.inventory.getCurrentItem();
                                if (item == null || !(item.getItem() instanceof ItemBlock)) {
                                    this.silentSlot = -1;
                                }
                            }

                            if (slient.getObject() && (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) || switchBlocks.getObject()) {
                                for (int i = 0; i < 9; i++) {
                                    final ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                    if (item != null && item.getItem() instanceof ItemBlock) {
                                        if (!blackList.contains(Block.getBlockFromItem(item.getItem()))) {
                                            if ((!switchBlocks.getObject() || !switchedSlots.contains(i)) && (this.silentSlot == -1 || switchBlocks.getObject() && !switchedSlots.contains(i))) {
                                                if (mc.thePlayer.inventory.currentItem != i) {
                                                    itemStack = item;
                                                    if (silentSlot != i) {
                                                        mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
                                                    }
                                                    this.silentSlot = i;
                                                    hasSilent = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                this.silentSlot = mc.thePlayer.inventory.currentItem;
                                hasSilent = true;
                            }


                            if (this.silentSlot != -1) {
                                itemStack = mc.thePlayer.inventory.getStackInSlot(this.silentSlot);
                            }

                            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                                Vec3 vec3 = new Vec3(curPos.getX() + 0.5, curPos.getY() + 0.5, curPos.getZ() + 0.5);
                                if (automaticVector.getObject() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                                    vec3 = mc.objectMouseOver.hitVec;
                                if (!blockCheck.getObject() || mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK || mc.objectMouseOver != null && allowAir.getObject() && mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
                                    if ((!rayCast.getObject() || !facingCheck.getObject()) || mc.objectMouseOver != null && mc.objectMouseOver.sideHit == enumFacing || (downScaffold.getObject() && mc.gameSettings.keyBindSneak.isKeyDown()))
                                        if (!sameY.getObject() || !rayCast.getObject() || (((blockpos.getY() == startY - 1 || !PlayerUtils.isMoving2() && mc.gameSettings.keyBindJump.isKeyDown()) && (!mc.objectMouseOver.sideHit.equals(EnumFacing.UP))) || !PlayerUtils.isMoving2()))
                                            if (!canUpCheck.getObject() || enumFacing != EnumFacing.UP || (mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null && mc.objectMouseOver.getBlockPos().equals(blockpos))) {
                                                if (rayCast.getObject() ? (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) : mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, curPos, enumFacing, vec3)) {

                                                    flag = false;

                                                    if (!noSwing.getObject())
                                                        mc.thePlayer.swingItem();
                                                    else
                                                        mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());

                                                    int blocks = 0;

                                                    if (switchBlocks.getObject()) {
                                                        for (int i = 0; i < 9; i++) {
                                                            final ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                                            if (item != null && item.getItem() instanceof ItemBlock) {
                                                                if (!blackList.contains(Block.getBlockFromItem(item.getItem()))) {
                                                                    blocks++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (itemSwitchTimer.hasReached(switchDelay.getObject()) || !switchBlocks.getObject()) {
                                                        if (silentSlot != -1 && slient.getObject() && switchBlocks.getObject() && blocks > 1)
                                                            switchedSlots.add(silentSlot);
                                                        if (blocks <= switchedSlots.size())
                                                            switchedSlots.clear();
                                                        itemSwitchTimer.reset();
                                                    }
                                                }
                                            }
                            } else {
                                return;
                            }
                            timeHelper.reset();
                        }
                        if (itemStack != null && itemStack.stackSize == 0 && this.silentSlot != -1) {
                            mc.thePlayer.inventory.mainInventory[this.silentSlot] = null;
                        }
                        if (flag && !mistake.getObject() && utils.randomInRange(0, 100) <= mistakerate.getObject() && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                            if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemStack)) {
                                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                            }
                        }
                    }
                }

        }

    }

    @EventTarget
    private void onPacket(PacketEvent e) {
        if (slient.getObject()) {
            if (e.getPacket() instanceof C09PacketHeldItemChange) {
                e.setCancelled(true);
            }
        }
    }

    @EventTarget
    public void onMove(PlayerMoveEvent event) {
    }


    @EventTarget
    private void onUpdate(UpdateEvent e) {
        if (sprint.getObject()) {
            mc.thePlayer.setSprinting(true);
        }
        ((IAccessorKeyBinding) mc.gameSettings.keyBindSprint).setPressed(false);

        if (sameY.getObject())
            if (!mc.thePlayer.onGround)
                mc.thePlayer.jumpMovementFactor = 0.02f;
            else {
                if (jump.getObject() && PlayerUtils.isMoving2())
                    mc.thePlayer.jump();
            }
    }

    @EventTarget
    private void onJump(JumpEvent e) {
        //  if (jump.getObject()) e.setCancelled(true);
    }

    public BlockPos searchPos(BlockPos pos) {
        for (int x = -1; x < 1; x++)
            for (int z = -1; z < 1; z++) {
                final BlockPos find = pos.add(x, 0, z);
                if (mc.theWorld.getBlockState(find).getBlock() == Blocks.air) {
                    return find;
                }
            }
        return null;
    }

    private float[] bestVector(BlockPos position, float pitch, int reach) {
        for (float yaw = (int) curYaw; yaw < curYaw + 360; yaw += 0.1) {
            final MovingObjectPosition movingObjectPosition = utils.rayTrace(yaw, pitch, reach);
            if (movingObjectPosition.sideHit == enumFacing && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                ChatUtils.info(movingObjectPosition.hitVec.xCoord + " " + movingObjectPosition.hitVec.yCoord + " " + movingObjectPosition.hitVec.zCoord);
                final BlockPos blockPos = movingObjectPosition.getBlockPos();
                return new float[]{yaw, pitch};
            }
        }
        return null;
    }

    public Vec3 calcShouldYaw() {
        BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        Vec3 vec2 = (Vec3) utils.getVectorForRotation(0, mc.thePlayer.rotationYaw);
        Vec3 v = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        Vec3 vec = (Vec3) v.addVector(0.5, 0, 0.5);
        return (Vec3) vec.addVector(Math.round(vec2.xCoord), 0, Math.round(vec2.zCoord));
    }

    public Vec3 expand(Vec3 position) {
        if (expand.getObject()) {
            final double direction = allDirectionExpand.getObject() ? MoveUtils.getDirection(mc.thePlayer.rotationYaw) : mc.thePlayer.rotationYaw;
            final Vec3 expandVector = new Vec3(-Math.sin(direction / 180 * Math.PI), 0, Math.cos(direction / 180 * Math.PI));
            int bestExpand = 0;
            for (int i = 0; i < expandLength.getObject(); i++) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && !PlayerUtils.isMoving2())
                    break;
                if (getBlockPosToPlaceOn(new BlockPos(position.addVector(0, -1, 0).add(new Vec3(expandVector.xCoord * i, expandVector.yCoord * i, expandVector.zCoord * i)))) != null && enumFacing != EnumFacing.UP) {
                    bestExpand = i;
                }
            }
            position = (Vec3) position.add(new Vec3(expandVector.xCoord * bestExpand, expandVector.yCoord * bestExpand, expandVector.zCoord * bestExpand));
        }
        return position;
    }


    //TODO : Improve it due to Sigma paste theme
    private BlockPos getBlockPosToPlaceOn(BlockPos pos) {
        final BlockPos blockPos1 = pos.add(-1, 0, 0);
        final BlockPos blockPos2 = pos.add(1, 0, 0);
        final BlockPos blockPos3 = pos.add(0, 0, -1);
        final BlockPos blockPos4 = pos.add(0, 0, 1);
        final boolean isDown = downScaffold.getObject() && utils.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        if (isDown)
            ((IAccessorKeyBinding) mc.gameSettings.keyBindSneak).setPressed(false);
        final float down = isDown ? 1 : 0;
        if (upScaffold.getObject() && mc.theWorld.getBlockState(pos.add(0, -1 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.UP;
            return (pos.add(0, -1 - down, 0));
        } else if (isDown && mc.theWorld.getBlockState(pos).getBlock() != Blocks.air) {
            //BLOCK DOWN
            enumFacing = EnumFacing.DOWN;
            return (pos);
        } else if (mc.theWorld.getBlockState(pos.add(-1, 0 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.EAST;
            return (pos.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(pos.add(1, 0 - down, 0)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.WEST;
            return (pos.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(pos.add(0, 0 - down, -1)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.SOUTH;
            return (pos.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(pos.add(0, 0 - down, 1)).getBlock() != Blocks.air) {
            enumFacing = EnumFacing.NORTH;
            return (pos.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.DOWN;
            return (blockPos1.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos1.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos1.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos1.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos1.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.UP;
            return (blockPos2.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos2.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos2.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos2.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos2.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.UP;
            return (blockPos3.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos3.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos3.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos3.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos3.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.UP;
            return (blockPos4.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos4.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos4.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos4.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getObject()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos4.add(0, 0 - down, 1));
        }
        return null;
    }

}
