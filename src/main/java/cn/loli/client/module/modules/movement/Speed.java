package cn.loli.client.module.modules.movement;

import cn.loli.client.events.JumpEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class Speed extends Module {

    private enum MODE {
        MINI("Mini"), PACKET_ABUSING("PacketAbusing"), ZOOM("Zoom"), BUNNY("Bunny");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public final EnumProperty modes = new EnumProperty<>("Mode", MODE.BUNNY);
    private final NumberProperty<Float> multiply = new NumberProperty<>("Multiply", 1f, 1f, 2f, 0.1f);
    private final BooleanProperty boost = new BooleanProperty("Boost", true);
    private final BooleanProperty clips = new BooleanProperty("Clips", true);
    private final BooleanProperty ySpoof = new BooleanProperty("Y Spoof", true);

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

    private final IEventListener<UpdateEvent> onUpdate = e ->
    {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if ("PacketAbusing".equals(modes.getPropertyValue())) {
            ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
            if (mc.thePlayer.onGround) {
                if (mc.thePlayer.ticksExisted % 20 == 0) {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 15000.0F;
                } else {
                    ((IAccessorMinecraft) mc).getTimer().timerSpeed = 1.0F;
                }
            }
        }
    };

    private final IEventListener<PlayerMoveEvent> onMove = event ->
    {
        switch (modes.getPropertyValue().toString()) {
            case "Mini": {
                if (playerUtils.isMoving2()) {
                    if (playerUtils.isInLiquid()) return;
                    if (boost.getPropertyValue()) {
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
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (0.11D * multiply.getPropertyValue()), mc.thePlayer.posZ);
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
    };

    private final IEventListener<MotionUpdateEvent> onMoveUpdate = event ->
    {
        if (event.getEventType() == EventType.PRE) {
            switch (modes.getPropertyValue().toString()) {
                case "Mini": {
                    if (playerUtils.isMoving2()) {
                        if (playerUtils.isInLiquid())
                            return;

                        double motionX = mc.gameSettings.keyBindForward.isKeyDown() && clips.getPropertyValue() ? (MathHelper.sin((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;
                        double motionZ = mc.gameSettings.keyBindForward.isKeyDown() && clips.getPropertyValue() ? (MathHelper.cos((float) Math.toRadians(mc.thePlayer.rotationYaw)) * 0.065) : 0;

                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX - motionX, mc.thePlayer.posY + (0.11D * multiply.getPropertyValue()), mc.thePlayer.posZ + motionZ);
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

            if (ySpoof.getPropertyValue())
                if (mc.thePlayer.onGround)
                    event.setY(event.getY() + 2.40201E-6D);
        }
    };

    private final IEventListener<JumpEvent> onJump = event ->
    {
        event.setCancelled(true);
    };


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
