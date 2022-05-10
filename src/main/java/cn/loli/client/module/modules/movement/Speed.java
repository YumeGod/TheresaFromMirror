package cn.loli.client.module.modules.movement;

import cn.loli.client.events.JumpEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import dev.xix.event.EventType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class Speed extends Module {

    public final ModeValue modes = new ModeValue("Mode", "Mini", "PacketAbusing", "Mini", "Zoom", "Bunny");
    private final NumberValue<Float> multiply = new NumberValue<>("Multiply", 1f, 1f, 2f);
    private final BooleanValue boost = new BooleanValue("Boost", true);
    private final BooleanValue clips = new BooleanValue("Clips", true);
    private final BooleanValue ySpoof = new BooleanValue("Y Spoof", true);

    double distance;
    int stage;
    double speed, less;
    boolean wasOnGround;
    float yaw;
    int failTimes;

    public Speed() {
        super("Speed", "Just You Speed Boost", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            ((IAccessorEntityPlayer) mc.thePlayer).setSpeedInAir(0.02F);
            stage = 0;
            speed = 0;
            less = 0;
            distance = 0;
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if ("PacketAbusing".equals(modes.getCurrentMode())) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            if (mc.thePlayer.onGround) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 15000.0F;
                } else {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                }
            }
        }
    }

    @EventTarget
    private void onMove(PlayerMoveEvent event) {
        switch (modes.getCurrentMode()) {
            case "Mini": {
                if (playerUtils.isMoving2()) {
                    if (playerUtils.isInLiquid()) return;
                    if (boost.getObject()) {
                        speed = getLegitSpeed(stage) * 0.96;
                        if (speed < moveUtils.getBaseMoveSpeed())
                            speed = moveUtils.getBaseMoveSpeed();
                    } else {
                        speed = moveUtils.getBaseMoveSpeed();
                    }

                    moveUtils.setMotion(event, speed);
                    stage++;
                }
                break;
            }
            case "Zoom": {
                if (playerUtils.isMoving2() && playerUtils.isOnGround(0.01) && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + event.getX(), mc.thePlayer.posY, mc.thePlayer.posZ + event.getZ(), true));
                    event.setX(event.getX() * 2);
                    event.setZ(event.getZ() * 2);
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.11D * multiply.getObject()), mc.thePlayer.posZ);
                } else {
                    moveUtils.setMotion(event, moveUtils.getBaseMoveSpeed(0.2691, 0.2));
                }
                break;
            }
            case "Bunny": {
                if (playerUtils.isMoving2()) {
                    double baseMoveSpeed = moveUtils.getBaseMoveSpeed(0.2871, 0.2);
                    boolean shouldLowhop = !mc.gameSettings.keyBindJump.isKeyDown() &&
                            !mc.thePlayer.isPotionActive(Potion.jump) && !mc.thePlayer.isCollidedHorizontally;

                    if (!mc.thePlayer.onGround && shouldLowhop && mc.thePlayer.fallDistance < 0.54)
                        event.setY(mc.thePlayer.motionY = lowHopYModification(mc.thePlayer.motionY, moveUtils.round(mc.thePlayer.posY - (int) mc.thePlayer.posY, 0.001)));

                    if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !wasOnGround) {
                        speed = baseMoveSpeed * 1.7;
                        event.setY(mc.thePlayer.motionY = shouldLowhop ? 0.4F : moveUtils.getJumpHeight(mc.thePlayer));
                        wasOnGround = true;
                    } else if (wasOnGround) {
                        wasOnGround = false;
                        final double bunnySlope = 0.66 * (distance - baseMoveSpeed);
                        speed = distance - bunnySlope;
                    } else {
                        speed = moveUtils.applyNCPFriction(mc.thePlayer, speed, distance, baseMoveSpeed);
                    }

                    speed = Math.max(speed, baseMoveSpeed);

                    if (failTimes > 0 && failTimes % 2 == 0) speed = speed * 0.9;

                    moveUtils.setSpeed(mc.thePlayer, event, targetStrafeInstance, speed);
                }

                break;
            }
        }

    }

    @EventTarget
    private void onMove(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            switch (modes.getCurrentMode()) {
                case "Mini": {
                    if (playerUtils.isMoving2()) {
                        if (playerUtils.isInLiquid())
                            return;

                        double motionX = mc.gameSettings.keyBindForward.isKeyDown() && clips.getObject() ? (MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;
                        double motionZ = mc.gameSettings.keyBindForward.isKeyDown() && clips.getObject() ? (MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;

                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX - motionX, mc.thePlayer.posY + (0.11D * multiply.getObject()), mc.thePlayer.posZ + motionZ);
                        }
                    }
                    break;
                }
                case "Bunny": {
                    double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
                    double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
                    distance = Math.sqrt(xDist * xDist + zDist * zDist);
                    if (failTimes > 0) failTimes--;
                    float yaw = (float) (Math.toDegrees(Math.atan2(zDist, xDist)) - 90.0);
                    if ((Math.abs(this.yaw - yaw)) >= 45) failTimes = 5;
                    this.yaw = yaw;
                    break;
                }
            }

            if (ySpoof.getObject())
                if (mc.thePlayer.onGround)
                    event.setY(event.getY() + 2.40201E-6D);
        }


    }

    @EventTarget
    private void onJump(JumpEvent event) {
        event.setCancelled(true);
    }


    private double getLegitSpeed(int stage) {
        double base = 0;

        final double init = moveUtils.getBaseMoveSpeed() + (moveUtils.getSpeedEffect() * 0.075);
        final double slowDown = init - (double) stage / 500.0 * 3;

        if (stage == 0) base = init;
        else if (stage >= 1) base = slowDown;

        return Math.max(base, moveUtils.getBaseMoveSpeed());
    }

    private double lowHopYModification(final double baseMotionY,
                                       final double yDistFromGround) {
        if (yDistFromGround == moveUtils.LOW_HOP_Y_POSITIONS[0]) {
            return 0.31;
        } else if (yDistFromGround == moveUtils.LOW_HOP_Y_POSITIONS[1]) {
            return 0.04;
        } else if (yDistFromGround == moveUtils.LOW_HOP_Y_POSITIONS[2]) {
            return -0.2;
        } else if (yDistFromGround == moveUtils.LOW_HOP_Y_POSITIONS[3]) {
            return -0.14;
        } else if (yDistFromGround == moveUtils.LOW_HOP_Y_POSITIONS[4]) {
            return -0.2;
        }

        return baseMotionY;
    }
}
