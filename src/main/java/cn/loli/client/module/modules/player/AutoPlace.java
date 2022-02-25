package cn.loli.client.module.modules.player;

import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.rotation.Rotation;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AutoPlace extends Module {

    private final ModeValue mode = new ModeValue("Place Mode", "Update", "Update", "Instant");
    private final ModeValue placetype = new ModeValue("Place Type", "Slient", "Slient", "Legit");


    public AutoPlace() {
        super("Auto Place", "Make you able to place blocks quickly", ModuleCategory.PLAYER);
    }

    @EventTarget
    private void onPlace(RenderEvent event) {
        if (mode.getCurrentMode().equalsIgnoreCase("Instant"))
            onPlace();
    }

    @EventTarget
    private void onPlace(UpdateEvent event) {
        if (mode.getCurrentMode().equalsIgnoreCase("Update"))
            onPlace();
    }

    private void onPlace() {
        if (isHoldingBlock()) {
            if (placetype.getCurrentMode().equalsIgnoreCase("Slient")) {
                final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                final Vec3 rotationVector = getVectorForRotation(new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
                final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                if (obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), obj.getBlockPos(), obj.sideHit, obj.hitVec);
                    mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
                }
            } else
                ((IAccessorMinecraft) mc).invokeRightClickMouse();
        }
    }

    private static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }


    private boolean isHoldingBlock() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().stackSize > 0 && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
    }


    @Override
    protected void onEnable() {
        
    }


    @Override
    protected void onDisable() {
        
    }
}
