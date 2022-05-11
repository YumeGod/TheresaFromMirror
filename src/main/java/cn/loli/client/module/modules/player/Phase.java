package cn.loli.client.module.modules.player;

import cn.loli.client.events.CollisionEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import static java.lang.StrictMath.toRadians;

public class Phase extends Module {

    public Phase() {
        super("Phase", "Make you throught a block", ModuleCategory.PLAYER);
    }


    public static boolean isInsideBlock() {
        final EntityPlayerSP player = mc.thePlayer;
        final WorldClient world = mc.theWorld;
        final AxisAlignedBB bb = player.getEntityBoundingBox();
        for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
                    final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)))) != null && player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final IEventListener<CollisionEvent> onCollide = event ->
    {
        if (isInsideBlock())
            event.setBoundingBox(null);
    };


    private final IEventListener<PlayerMoveEvent> onMove = event ->
    {
        if (isInsideBlock()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(mc.thePlayer.motionY += 0.09f);
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.setY(mc.thePlayer.motionY -= 0.09f);
            } else {
                event.setY(mc.thePlayer.motionY = 0.0f);
            }
            moveUtils.setMotion(event, moveUtils.getBaseMoveSpeed());
        }
    };

    private final IEventListener<MotionUpdateEvent> onPost = event ->
    {
        if (event.getEventType() == EventType.POST) {
            if (mc.thePlayer.stepHeight > 0) mc.thePlayer.stepHeight = 0;

            float moveStrafe = mc.thePlayer.movementInput.moveStrafe, // @off
                    moveForward = mc.thePlayer.movementInput.moveForward,
                    rotationYaw = mc.thePlayer.rotationYaw;

            double multiplier = 0.3,
                    mx = -MathHelper.sin((float) toRadians(rotationYaw)),
                    mz = MathHelper.cos((float) toRadians(rotationYaw)),
                    x = moveForward * multiplier * mx + moveStrafe * multiplier * mz,
                    z = moveForward * multiplier * mz - moveStrafe * multiplier * mx; // @on

            if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && mc.thePlayer.onGround) {
                double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX + x, posY, posZ + z, true));
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + 3, posZ, true));
                mc.thePlayer.setPosition(posX + x, posY, posZ + z);
            }
        }
    };



    @Override
    protected void onEnable() {
        
    }

    @Override
    protected void onDisable() {
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = 0.6f;

        
    }

}
