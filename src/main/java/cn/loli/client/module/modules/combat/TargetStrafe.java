package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.MouseOverEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class TargetStrafe extends Module {

    private enum MODE {
        CIRCLE("Circle"), FREEZE("Freeze");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Need to be holding space
    public final BooleanProperty holdSpaceProperty = new BooleanProperty("Hold Space", true);
    // Pattern mode
    private final EnumProperty modeProperty = new EnumProperty<>("Mode", MODE.CIRCLE);
    // Radius & points
    private final NumberProperty<Integer> pointsProperty = new NumberProperty<>("Points", 12, 1, 18, 1);
    private final NumberProperty<Double> radiusProperty = new NumberProperty<>("Radius", 2.0, 0.1, 6.0, 0.1);
    // Adaptive
    private final BooleanProperty adaptiveSpeedProperty = new BooleanProperty("Adapt Speed", true);
    //Direction Keys
    private final BooleanProperty directionKeyProperty = new BooleanProperty("Direction Keys", true);

    private final List<Point> currentPoints = new ArrayList<>();
    public EntityLivingBase currentTarget;
    private int direction = 1;
    private Point currentPoint;

    public TargetStrafe() {
        super("Target Strafe", "Make you strafe to some bitch", ModuleCategory.COMBAT);
        targetStrafeInstance = this;
    }

    private final IEventListener<RenderEvent> onUpdate = e ->
    {
        this.currentTarget = Main.INSTANCE.moduleManager.getModule(Aura.class).target;

        if (this.currentTarget != null) {
            if (this.directionKeyProperty.getPropertyValue()) {
                if (mc.gameSettings.keyBindLeft.isPressed()) {
                    this.direction = 1;
                }

                if (mc.gameSettings.keyBindRight.isPressed()) {
                    this.direction = -1;
                }
            }

            this.collectPoints(this.pointsProperty.getPropertyValue() * radiusProperty.getPropertyValue().intValue(), this.radiusProperty.getPropertyValue(), this.currentTarget);
            this.currentPoint = this.findOptimalPoint(this.currentTarget, this.currentPoints);
        } else {
            this.currentPoint = null;
        }
    };


    private Point findOptimalPoint(EntityLivingBase target, List<Point> points) {
        if ("Freeze".equals(modeProperty.getPropertyValue().toString())) {
            return getClosestPoint(mc.thePlayer.posX, mc.thePlayer.posZ, points);
        }
        final Point closest = getClosestPoint(mc.thePlayer.posX, mc.thePlayer.posZ, points);

        if (closest == null)
            return null;

        final int pointsSize = points.size();

        if (pointsSize == 1)
            return closest;

        final int closestIndex = points.indexOf(closest);

        Point nextPoint;

        int passes = 0;

        do {
            if (passes > pointsSize) // Note :: Shit fix
                return null;
            int nextIndex = closestIndex + this.direction;
            if (nextIndex < 0) nextIndex = pointsSize - 1;
            else if (nextIndex >= pointsSize) nextIndex = 0;

            nextPoint = points.get(nextIndex);

            if (!nextPoint.valid)
                this.direction = -this.direction;
            ++passes;
        } while (!nextPoint.valid);

        return nextPoint;
    }

    private void collectPoints(final int size,
                               final double radius,
                               final EntityLivingBase entity) {
        this.currentPoints.clear();

        final double x = entity.posX;
        final double z = entity.posZ;

        final double pix2 = Math.PI * 2.0;

        for (int i = 0; i < size; i++) {
            double cos = radius * StrictMath.cos(i * pix2 / size);
            double sin = radius * StrictMath.sin(i * pix2 / size);

            final Point point = new Point(entity,
                    new Vec3(cos, 0, sin),
                    this.validatePoint(new Vec3(x + cos, entity.posY, z + sin)));

            this.currentPoints.add(point);
        }
    }

    private static Point getClosestPoint(final double srcX, final double srcZ, List<Point> points) {
        double closest = Double.MAX_VALUE;
        Point bestPoint = null;

        for (Point point : points) {
            if (point.valid) {
                final double dist = playerUtils.distance(srcX, srcZ, point.point.xCoord, point.point.zCoord);
                if (dist < closest) {
                    closest = dist;
                    bestPoint = point;
                }
            }
        }

        return bestPoint;
    }

    private boolean validatePoint(final Vec3 point) {

        final MovingObjectPosition rayTraceResult = mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionVector(), point,
                false, true, false);

        if (rayTraceResult != null && rayTraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return false;

        // TODO :: Replace this with bb check

        final BlockPos pointPos = new BlockPos(point);
        final IBlockState blockState = mc.theWorld.getBlockState(pointPos);

        if (blockState.getBlock().canCollideCheck(blockState, false) && !blockState.getBlock().isPassable(mc.theWorld, pointPos))
            return false;

        final IBlockState blockStateAbove = mc.theWorld.getBlockState(pointPos.add(0, 1, 0));

        return !blockStateAbove.getBlock().canCollideCheck(blockState, false) &&
                !isOverVoid(point.xCoord, Math.min(point.yCoord, mc.thePlayer.posY), point.zCoord);
    }

    private boolean isOverVoid(final double x,
                               final double y,
                               final double z) {
        for (double posY = y; posY > 0.0; posY--) {
            final IBlockState state = mc.theWorld.getBlockState(new BlockPos(x, posY, z));
            if (state.getBlock().canCollideCheck(state, false)) {
                return y - posY > 2;
            }
        }

        return true;
    }

    public boolean isCloseToPoint(final Point point) {
        return playerUtils.distance(mc.thePlayer.posX, mc.thePlayer.posZ, point.point.xCoord, point.point.zCoord) < 0.2;
    }

    public boolean shouldAdaptSpeed() {
        if (!adaptiveSpeedProperty.getPropertyValue())
            return false;
        return isCloseToPoint(currentPoint);
    }

    public double getAdaptedSpeed() {
        final EntityLivingBase entity = currentTarget;
        if (entity == null) return 0.0;
        return playerUtils.distance(entity.prevPosX, entity.prevPosZ, entity.posX, entity.posZ);
    }

    public boolean shouldStrafe() {
        return Main.INSTANCE.moduleManager.getModule(TargetStrafe.class).getState() &&
                (!holdSpaceProperty.getPropertyValue() || Keyboard.isKeyDown(Keyboard.KEY_SPACE)) &&
                currentTarget != null &&
                currentPoint != null;
    }

    public void setSpeed(final PlayerMoveEvent event, final double speed) {
        final Point point = currentPoint;
        moveUtils.setSpeed(event, speed, 1, 0,
                rotationUtils.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw,
                        mc.thePlayer.posX, mc.thePlayer.posZ,
                        point.point.xCoord, point.point.zCoord));
    }


    private static final class Point {
        private final EntityLivingBase entity;
        private final Vec3 posOffset;
        private final Vec3 point;
        private final boolean valid;

        public Point(final EntityLivingBase entity,
                     final Vec3 posOffset,
                     final boolean valid) {
            this.entity = entity;
            this.posOffset = posOffset;
            this.valid = valid;

            this.point = this.calculatePos();
        }

        private Vec3 calculatePos() {
            return entity.getPositionVector().add(this.posOffset);
        }

        private Vec3 calculateInterpolatedPos(final float partialTicks) {
            final double x = interpolate(entity.prevPosX, entity.posX, partialTicks);
            final double y = interpolate(entity.prevPosY, entity.posY, partialTicks);
            final double z = interpolate(entity.prevPosZ, entity.posZ, partialTicks);

            final Vec3 interpolatedEntity = new Vec3(x, y, z);

            return interpolatedEntity.add(posOffset);
        }

        private double interpolate(final double old, final double now, final double progress) {
            return old + (now - old) * progress;
        }
    }
}
