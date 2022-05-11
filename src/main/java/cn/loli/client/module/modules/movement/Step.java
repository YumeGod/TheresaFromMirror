package cn.loli.client.module.modules.movement;

import cn.loli.client.events.StepEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class Step extends Module {

    TimeHelper timer = new TimeHelper();
    boolean resetTimer;

    public Step() {
        super("Step", "Make you able to step on blocks", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null)
            mc.thePlayer.stepHeight = 0.625f;

        super.onDisable();
    }

    private final IEventListener<StepEvent> onStep = event ->
    {
        if (event.getEventType() == EventType.PRE) {
            if (this.resetTimer) {
                this.resetTimer = false;
            }
            if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isKeyDown() && timer.hasReached(100)) {
                event.setStepHeight(1.5F);
            }
        }
        if (event.getEventType() == EventType.POST) {
            final double realHeight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
            if (realHeight >= .625) {
                timer.reset();
                resetTimer = true;
                doNCPStep(realHeight);
            }
        }
    };


    private void doNCPStep(double height) {
        final double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;

        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        mc.thePlayer.setSprinting(false);

        if (height <= 1) {

            final float[] values = {
                    .42F,
                    .75F
            };

            if (height != 1) {
                values[0] *= height;
                values[1] *= height;

                if (values[0] > .425) values[0] = .425F;
                if (values[1] > .78) values[1] = .78F;
                if (values[1] < .49) values[1] = .49F;
            }

            if (values[0] == .42) values[0] = .41999998688698F;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[0], posZ, false));

            if (posY + values[1] < posY + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + values[1], posZ, false));
        } else if (height <= 1.5) {

            final float[] values = {
                    .41999998688698F,
                    .7531999805212F,
                    1.00133597911215F,
                    1.06083597911215F,
                    0.9824359775862711F
            };

            for (double val : values)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + val, posZ, false));
        }

        mc.getNetHandler().getNetworkManager().sendPacket(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        mc.thePlayer.stepHeight = 0.625F;
    }
}
