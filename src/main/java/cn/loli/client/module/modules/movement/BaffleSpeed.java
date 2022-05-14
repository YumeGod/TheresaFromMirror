package cn.loli.client.module.modules.movement;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.movement.MoveUtils;
import cn.loli.client.utils.player.PlayerUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class BaffleSpeed extends Module {

    private final BooleanProperty spoof = new BooleanProperty("Spoof Jump", false);
    private final BooleanProperty boost = new BooleanProperty("Boost", false);
    private static final NumberProperty<Float> speed = new NumberProperty<>("Boost Speed", 0.2f, 0.1f, 1f , 0.1f);

    boolean wasObstacle;

    public BaffleSpeed() {
        super("BaffleSpeed", "You are fast under blocks", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        wasObstacle = false;

    }

    @Override
    public void onDisable() {

    }

    private final IEventListener<UpdateEvent> onUpdate = event ->
    {
        if (playerUtils.isMoving2()) {
            if ((mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.5, mc.thePlayer.posZ)).getBlock() != Blocks.air)) {
                wasObstacle = true;
                if (mc.thePlayer.onGround) {
                    if (boost.getPropertyValue()) {
                        moveUtils.setSpeed(speed.getPropertyValue());
                    }
                    if (spoof.getPropertyValue())
                        mc.getNetHandler().getNetworkManager().sendPacket
                                (new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42, mc.thePlayer.posZ, false));
                    else
                        mc.thePlayer.jump();
                }

            } else {
                if (wasObstacle)
                    wasObstacle = false;
            }

        } else {

            wasObstacle = false;
        }

    };


}
