package cn.loli.client.module.modules.player;

import cn.loli.client.events.*;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    private final NumberProperty<Integer> delay = new NumberProperty<>("Delay", 0, 0, 500, 10);
    private final BooleanProperty random = new BooleanProperty("Delay-Randomize", false);

    private final BooleanProperty slient = new BooleanProperty("Slient", false);
    private final BooleanProperty expand = new BooleanProperty("Expend", false);
    private final BooleanProperty diagonal = new BooleanProperty("Diagonal", false);

    private final BooleanProperty mouseFix = new BooleanProperty("Mouse Fix", true);
    private final BooleanProperty mouse_vl_fix = new BooleanProperty("Mouse VL Fix", true);

    private final NumberProperty<Integer> expandLength = new NumberProperty<>("Expand Length", 0, 0, 6, 1);
    private final BooleanProperty allDirectionExpand = new BooleanProperty("All Direction", true);

    private final BooleanProperty rotation = new BooleanProperty("Rotation", false);
    private final BooleanProperty keeprotation = new BooleanProperty("Keep Rotation", false);

    private final BooleanProperty dynamicYaw = new BooleanProperty("Dynamic Yaw", false);
    private final BooleanProperty randomizePitch = new BooleanProperty("Randomize Pitch", false);

    private final BooleanProperty staticPitch = new BooleanProperty("Static Pitch", false);
    private final NumberProperty<Integer> pitch = new NumberProperty<>("Static Pitch Range", 80, 70, 90, 1);
    private final NumberProperty<Integer> switchDelay = new NumberProperty<>("Switch Delay", 0, 0, 1000, 10);


    private final BooleanProperty rayCast = new BooleanProperty("Ray Cast", false);
    private final BooleanProperty clampYaw = new BooleanProperty("Clamp", true);

    private final BooleanProperty moveFix = new BooleanProperty("Move Fix", false);
    private final BooleanProperty silentMoveFix = new BooleanProperty("Silent Fix", false);
    private final BooleanProperty shouldyaw = new BooleanProperty("Silent Yaw Fix", false);

    private final BooleanProperty upScaffold = new BooleanProperty("UP-Scaffold", false);
    private final BooleanProperty downScaffold = new BooleanProperty("Down-Scaffold", false);

    private final BooleanProperty sprint = new BooleanProperty("Sprint", true);
    private final BooleanProperty jump = new BooleanProperty("AutoJump", false);
    private final BooleanProperty sameY = new BooleanProperty("Same Y", false);

    private final BooleanProperty prediction = new BooleanProperty("Prediction", false);
    private final BooleanProperty randomAim = new BooleanProperty("Random Aim", false);

    private final BooleanProperty simple = new BooleanProperty("Simple", false);

    private final BooleanProperty rotateInAir = new BooleanProperty("Rotation When Air", false);

    private final BooleanProperty blockCheck = new BooleanProperty("Check Block", false);
    private final BooleanProperty allowAir = new BooleanProperty("Allow Air", false);
    private final BooleanProperty switchBlocks = new BooleanProperty("Block Picker", false);
    private final BooleanProperty automaticVector = new BooleanProperty("Auto HitVec", false);

    private final BooleanProperty facingCheck = new BooleanProperty("Facing Check", false);
    private final BooleanProperty canUpCheck = new BooleanProperty("Can UP Check", false);

    private final BooleanProperty noSwing = new BooleanProperty("No Swing", false);


    private final BooleanProperty mistake = new BooleanProperty("Mistake", false);
    private final NumberProperty<Integer> mistakerate = new NumberProperty<>("Mistake Rate", 80, 70, 90, 1);

    private enum MODE {
        TICK("On Tick"), PRE("On Pre"), POST("On Post");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty eventMode = new EnumProperty<>("Work on...", MODE.TICK);
    private final BooleanProperty sneak = new BooleanProperty("Sprint-Spoof", false);

    int silentSlot = -1;
    int startY;

    boolean canBuild, hasSilent;

    EnumFacing enumFacing;
    ItemStack itemStack;

    int currentBlocks;

    float curYaw, curPitch;

    private final List<Block> blackList;
    private final List<Integer> switchedSlots = new ArrayList<>();

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper switchTimer = new TimeHelper();
    private final TimeHelper itemSwitchTimer = new TimeHelper();

    BlockPos curPos;
    MovingObjectPosition ray;

    public Scaffold() {
        super("Scaffold", "Its place blocks under you", ModuleCategory.PLAYER);
        this.blackList =
                Arrays.asList(Blocks.waterlily, Blocks.heavy_weighted_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.red_flower, Blocks.yellow_flower, Blocks.crafting_table, Blocks.chest, Blocks.enchanting_table, Blocks.anvil, Blocks.sand, Blocks.gravel, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.ice, Blocks.packed_ice, Blocks.cobblestone_wall, Blocks.water, Blocks.lava, Blocks.web, Blocks.sapling, Blocks.rail, Blocks.golden_rail, Blocks.activator_rail, Blocks.detector_rail, Blocks.tnt, Blocks.red_flower, Blocks.yellow_flower, Blocks.flower_pot, Blocks.tallgrass, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.ladder, Blocks.torch, Blocks.stone_button, Blocks.wooden_button, Blocks.redstone_torch, Blocks.redstone_wire, Blocks.furnace, Blocks.cactus, Blocks.oak_fence, Blocks.acacia_fence, Blocks.nether_brick_fence, Blocks.birch_fence, Blocks.dark_oak_fence, Blocks.jungle_fence, Blocks.oak_fence, Blocks.acacia_fence_gate, Blocks.snow_layer, Blocks.trapdoor, Blocks.ender_chest, Blocks.trapped_chest, Blocks.beacon, Blocks.hopper, Blocks.daylight_detector, Blocks.daylight_detector_inverted, Blocks.carpet, Blocks.noteblock);
    }


    @Override
    public void onEnable() {
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

        if (slient.getPropertyValue())
            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

        switchTimer.reset();
    }

    private final IEventListener<RenderEvent> onRender = event ->
    {
        ray = rotationUtils.rayCastedBlock(curYaw, curPitch);

        if (curPos != null) {
            if ((!mc.thePlayer.onGround && rotateInAir.getPropertyValue()) || (ray == null || (ray.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || (!ray.getBlockPos().equals(curPos)) && ray.sideHit == enumFacing || (ray.sideHit != enumFacing && ray.getBlockPos().equals(curPos))))) {
                float[] rotation = rotationUtils.faceBlock(curPos, mc.theWorld.getBlockState(curPos).getBlock().getBlockBoundsMaxY() - mc.theWorld.getBlockState(curPos).getBlock().getBlockBoundsMinY() + 0.5D, mouse_vl_fix.getPropertyValue(), mouseFix.getPropertyValue(), prediction.getPropertyValue(), randomAim.getPropertyValue(), randomizePitch.getPropertyValue(), clampYaw.getPropertyValue(), 180);

                if (rotation != null)
                    if (simple.getPropertyValue()) {
                        curYaw = (dynamicYaw.getPropertyValue() ? moveUtils.getDirection(mc.thePlayer.rotationYaw) : mc.thePlayer.rotationYaw) + 180;
                        curPitch = mc.thePlayer.onGround || staticPitch.getPropertyValue() ? (float) pitch.getPropertyValue() : rotation[1];
                    } else {
                        curYaw = rotation[0];
                        if (!staticPitch.getPropertyValue())
                            curPitch = rotation[1];
                        else
                            curPitch = (float) pitch.getPropertyValue();
                    }

            }
        }
    };

    private final IEventListener<MoveFlyEvent> onMoveFly = event ->
    {
        if (moveFix.getPropertyValue() && rotation.getPropertyValue())
            event.setYaw(curYaw);
    };

    private final IEventListener<JumpYawEvent> onJump = event ->
    {
        if (moveFix.getPropertyValue() && rotation.getPropertyValue())
            event.setYaw(curYaw);
    };


    private final IEventListener<MovementStateEvent> onSilent = e ->
    {
        if (moveFix.getPropertyValue() && rotation.getPropertyValue() && silentMoveFix.getPropertyValue()) {
            e.setSilentMoveFix(true);
            e.setYaw(curYaw);
            if (shouldyaw.getPropertyValue()) {
                e.setShouldYaw(rotationUtils.getYaw(calcShouldYaw()) + 180);
                e.setFixYaw(true);
            }
        }
    };

    private final IEventListener<MotionUpdateEvent> onMotion = e ->
    {
        if (e.getEventType() == EventType.PRE) {
            if (rotation.getPropertyValue()) {
                if (keeprotation.getPropertyValue() || playerUtils.getBlockUnderPlayer(0.01F) == Blocks.air) {
                    e.setYaw(curYaw);
                    e.setPitch(curPitch + (float) (randomizePitch.getPropertyValue() ? playerUtils.randomInRange(-0.1, 0.1) : 0));
                }
            }

            if (eventMode.getPropertyValue().toString().equals("On Pre"))
                onWorking();
        } else {
            if (eventMode.getPropertyValue().toString().equals("On Post"))
                onWorking();
        }
    };

    private final IEventListener<TickAttackEvent> onTick = e ->
    {
        if (eventMode.getPropertyValue().toString().equals("On Tick"))
            onWorking();
    };


    private void onWorking() {
        final double y = sameY.getPropertyValue() && playerUtils.isMoving2() ? startY : mc.thePlayer.posY;

        if (!sprint.getPropertyValue()) {
            ((IAccessorKeyBinding) mc.gameSettings.keyBindSprint).setPressed(false);
            mc.thePlayer.setSprinting(false);
        }

        if ((!playerUtils.isMoving2() && mc.gameSettings.keyBindJump.isKeyDown()) || downScaffold.getPropertyValue() && playerUtils.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) || mc.thePlayer.onGround)
            startY = (int) mc.thePlayer.posY;

        boolean flag = true;


        Vec3 position = expand(mc.thePlayer.getPositionVector().addVector(0, (y - mc.thePlayer.posY), 0));
        this.curPos = getBlockPosToPlaceOn(new BlockPos(position.xCoord, position.yCoord - 1, position.zCoord));

        itemStack = mc.thePlayer.getCurrentEquippedItem();


        if (curPos != null) {
            if (!mc.thePlayer.isUsingItem())
                canBuild = true;


            if (blockCheck.getPropertyValue() && allowAir.getPropertyValue() || !rayCast.getPropertyValue()
                    || (ray != null && ray.getBlockPos() != null && mc.theWorld.getBlockState(ray.getBlockPos()).getBlock().getMaterial() != Material.air))
                if (!rayCast.getPropertyValue() || (ray != null && ray.getBlockPos() != null)) {
                    if (timeHelper.hasReached(delay.getPropertyValue())) {
                        final BlockPos blockpos = rayCast.getPropertyValue() ? ray.getBlockPos() : curPos;
                        if (blockpos != null && mc.theWorld.getBlockState(curPos) != null && mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            if (this.silentSlot != -1) {
                                final ItemStack item = mc.thePlayer.inventory.getCurrentItem();
                                if (item == null || !(item.getItem() instanceof ItemBlock)) {
                                    this.silentSlot = -1;
                                }
                            }

                            if (slient.getPropertyValue() && (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) || switchBlocks.getPropertyValue()) {
                                for (int i = 0; i < 9; i++) {
                                    final ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                    if (item != null && item.getItem() instanceof ItemBlock) {
                                        if (!blackList.contains(Block.getBlockFromItem(item.getItem()))) {
                                            if ((!switchBlocks.getPropertyValue() || !switchedSlots.contains(i)) && (this.silentSlot == -1 || switchBlocks.getPropertyValue() && !switchedSlots.contains(i))) {
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
                                if (automaticVector.getPropertyValue() && ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                                    vec3 = ray.hitVec;
                                if (!blockCheck.getPropertyValue() || ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK || ray != null && allowAir.getPropertyValue() && ray.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY)
                                    if ((!rayCast.getPropertyValue() || !facingCheck.getPropertyValue()) || ray != null && ray.sideHit == enumFacing || (downScaffold.getPropertyValue() && mc.gameSettings.keyBindSneak.isKeyDown()))
                                        if (!sameY.getPropertyValue() || !rayCast.getPropertyValue() || (((blockpos.getY() == startY - 1 || !playerUtils.isMoving2() && mc.gameSettings.keyBindJump.isKeyDown()) && (!ray.sideHit.equals(EnumFacing.UP))) || !playerUtils.isMoving2()))
                                            if (!canUpCheck.getPropertyValue() || enumFacing != EnumFacing.UP || (ray != null && ray.getBlockPos() != null && ray.getBlockPos().equals(blockpos))) {
                                                if (rayCast.getPropertyValue() ? (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, blockpos, ray.sideHit, ray.hitVec)) : mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, curPos, enumFacing, vec3)) {
                                                    flag = false;

                                                    if (!noSwing.getPropertyValue())
                                                        mc.thePlayer.swingItem();
                                                    else
                                                        mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());

                                                    int blocks = 0;

                                                    if (switchBlocks.getPropertyValue()) {
                                                        for (int i = 0; i < 9; i++) {
                                                            final ItemStack item = mc.thePlayer.inventory.getStackInSlot(i);
                                                            if (item != null && item.getItem() instanceof ItemBlock) {
                                                                if (!blackList.contains(Block.getBlockFromItem(item.getItem()))) {
                                                                    blocks++;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (itemSwitchTimer.hasReached(switchDelay.getPropertyValue()) || !switchBlocks.getPropertyValue()) {
                                                        if (silentSlot != -1 && slient.getPropertyValue() && switchBlocks.getPropertyValue() && blocks > 1)
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
                        if (flag && !mistake.getPropertyValue() && playerUtils.randomInRange(0, 100) <= mistakerate.getPropertyValue() && itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                            if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, itemStack)) {
                                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
                            }
                        }
                    }
                }

        }

    }

    private final IEventListener<PacketEvent> onPacket = e ->
    {
        if (sneak.getPropertyValue())
            if (e.getPacket() instanceof C0BPacketEntityAction) {
                final C0BPacketEntityAction c0B = (C0BPacketEntityAction) e.getPacket();

                if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket
                            (new C0BPacketEntityAction(Minecraft.getMinecraft().thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING), null);
                    e.setCancelled(true);
                }

                if (c0B.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                    e.setCancelled(true);
                }
            }
    };

    private final IEventListener<UpdateEvent> onUpdate = e ->
    {
        if (sprint.getPropertyValue()) {
            mc.thePlayer.setSprinting(true);
        }
        ((IAccessorKeyBinding) mc.gameSettings.keyBindSprint).setPressed(false);

        if (sameY.getPropertyValue())
            if (!mc.thePlayer.onGround)
                mc.thePlayer.jumpMovementFactor = 0.02f;
            else {
                if (jump.getPropertyValue() && playerUtils.isMoving2())
                    mc.thePlayer.jump();
            }
    };


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
            final MovingObjectPosition movingObjectPosition = rotationUtils.rayTrace(yaw, pitch, reach);
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
        Vec3 vec2 = (Vec3) rotationUtils.getVectorForRotation(0, mc.thePlayer.rotationYaw);
        Vec3 v = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        Vec3 vec = (Vec3) v.addVector(0.5, 0, 0.5);
        return (Vec3) vec.addVector(Math.round(vec2.xCoord), 0, Math.round(vec2.zCoord));
    }

    public Vec3 expand(Vec3 position) {
        if (expand.getPropertyValue()) {
            final double direction = allDirectionExpand.getPropertyValue() ? moveUtils.getDirection(mc.thePlayer.rotationYaw) : mc.thePlayer.rotationYaw;
            final Vec3 expandVector = new Vec3(-Math.sin(direction / 180 * Math.PI), 0, Math.cos(direction / 180 * Math.PI));
            int bestExpand = 0;
            for (int i = 0; i < expandLength.getPropertyValue(); i++) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && !playerUtils.isMoving2())
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
        final boolean isDown = downScaffold.getPropertyValue() && playerUtils.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
        if (isDown)
            ((IAccessorKeyBinding) mc.gameSettings.keyBindSneak).setPressed(false);
        final float down = isDown ? 1 : 0;
        if (upScaffold.getPropertyValue() && mc.theWorld.getBlockState(pos.add(0, -1 - down, 0)).getBlock() != Blocks.air) {
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
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.DOWN;
            return (blockPos1.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos1.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos1.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos1.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos1.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos1.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos2.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos2.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos2.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos2.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos2.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos2.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos3.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos3.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos3.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos3.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos3.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos3.add(0, 0 - down, 1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, -1 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.UP;
            return (blockPos4.add(0, -1 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(-1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.EAST;
            return (blockPos4.add(-1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(1, 0 - down, 0)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.WEST;
            return (blockPos4.add(1, 0 - down, 0));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, -1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.SOUTH;
            return (blockPos4.add(0, 0 - down, -1));
        } else if (mc.theWorld.getBlockState(blockPos4.add(0, 0 - down, 1)).getBlock() != Blocks.air && diagonal.getPropertyValue()) {
            enumFacing = EnumFacing.NORTH;
            return (blockPos4.add(0, 0 - down, 1));
        }
        return null;
    }

}
