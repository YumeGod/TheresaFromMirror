package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.MovementStateEvent;
import cn.loli.client.utils.player.rotation.RotationHook;

import dev.xix.TheresaClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MovementInputFromOptions.class)
public class MixinMovementInputFromOptions extends MovementInput {

    /**
     * @author Loli
     */
    @Overwrite
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (Minecraft.getMinecraft().gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (Minecraft.getMinecraft().gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (Minecraft.getMinecraft().gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }

        final MovementStateEvent event = new MovementStateEvent(moveForward, moveStrafe, RotationHook.yaw, Minecraft.getMinecraft().thePlayer.rotationYaw, false, false, Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown());
        Main.INSTANCE.eventBus.call(event);
        moveForward = event.getMoveForward();
        moveStrafe = event.getMoveStrafe();

        if (event.isSilentMoveFix() && (moveForward != 0 || moveStrafe != 0)) {
            getCorrectedMovement(moveForward, moveStrafe, event.getYaw(), event.getShouldYaw(), event.isFixYaw());
        }

        this.jump = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
        this.sneak = event.isSneak();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }


    public void getCorrectedMovement(float forward, float strafe, float yaw, float shouldYaw, boolean fixYaw) {
        float y = Minecraft.getMinecraft().thePlayer.rotationYaw;

        if (fixYaw)
            y = shouldYaw;

        if (fixYaw && yaw == y) return;

        float f4 = 0.91F;
        if (Minecraft.getMinecraft().thePlayer.onGround) {
            f4 = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posX), MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(Minecraft.getMinecraft().thePlayer.posZ))).getBlock().slipperiness * 0.91F;
        }
        float f5;
        if (Minecraft.getMinecraft().thePlayer.onGround) {
            f5 = Minecraft.getMinecraft().thePlayer.getAIMoveSpeed() * (0.16277136F / (f4 * f4 * f4));
        } else {
            f5 = Minecraft.getMinecraft().thePlayer.jumpMovementFactor;
        }
        float f = strafe * strafe + forward * forward;
        f = f5 / f;

        float fStrafe = strafe * f;
        float fForward = forward * f;
        float realYawSin = MathHelper.sin(y * (float) Math.PI / 180.0F);
        float realYawCos = MathHelper.cos(y * (float) Math.PI / 180.0F);
        // these are the correct motion values for the current rotation (ClientSide)
        float realYawMotionX = fStrafe * realYawCos - fForward * realYawSin;
        float realYawMotionZ = fForward * realYawCos + fStrafe * realYawSin;

        float rotationYawSin = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
        float rotationYawCos = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

        // NaN is just used for the initial usage of the array
        float[] closest = new float[]{Float.NaN, 0, 0};

        // now go thought -1, 0 and 1 and check for the value with the lowest distance to the correct motion values
        for (int possibleStrafe = -1; possibleStrafe <= 1; possibleStrafe++) {
            for (int possibleForward = -1; possibleForward <= 1; possibleForward++) {
                float testFStrafe = possibleStrafe * f;
                float testFForward = possibleForward * f;
                float testYawMotionX = testFStrafe * rotationYawCos - testFForward * rotationYawSin;
                float testYawMotionZ = testFForward * rotationYawCos + testFStrafe * rotationYawSin;
                // calculate the distance between the test motion and the real motion
                float diffX = realYawMotionX - testYawMotionX;
                float diffZ = realYawMotionZ - testYawMotionZ;
                float distance = MathHelper.sqrt_float(diffX * diffX + diffZ * diffZ);
                // compare the distance to the stored one to fine the nearest forward and strafe values
                if (Float.isNaN(closest[0]) || distance < closest[0]) {
                    closest[0] = distance;
                    closest[1] = possibleForward;
                    closest[2] = possibleStrafe;
                }
            }
        }
        moveForward = closest[1];
        moveStrafe = closest[2];
    }
}
