package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.implementations.IPlayerControllerMP;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpeedMine extends Module {

    private final NumberProperty<Float> speed = new NumberProperty<>("Speed", 1.6f, 0.8f, 3.0f , 0.1f);

    public BlockPos blockPos;
    public EnumFacing facing;
    public C07PacketPlayerDigging curPacket;

    private boolean bzs = false;
    private float bzx = 0.0f;

    public SpeedMine() {
        super("SpeedMine", "Make you dig faster", ModuleCategory.PLAYER);
    }

    private final IEventListener<UpdateEvent> onUpdate = e ->
    {
        if (mc.playerController.extendedReach()) {
            ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
        }
        else if (this.bzs) {
            final Block block = mc.theWorld.getBlockState(this.blockPos).getBlock();
            this.bzx += block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.blockPos) * speed.getPropertyValue();
            if (this.bzx >= 1.0f) {
                mc.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                this.bzx = 0.0f;
                this.bzs = false;
            }
        }
    };

    private final IEventListener<PacketEvent> onPacket = e ->
    {
        if (e.getPacket() instanceof C07PacketPlayerDigging && mc.playerController != null) {
            final C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging) e.getPacket();
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.bzs = true;
                this.blockPos = c07PacketPlayerDigging.getPosition();
                this.facing = c07PacketPlayerDigging.getFacing();
                this.bzx = 0.0f;
            }
            else if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.bzs = false;
                this.blockPos = null;
                this.facing = null;
            }
        }
    };


}
