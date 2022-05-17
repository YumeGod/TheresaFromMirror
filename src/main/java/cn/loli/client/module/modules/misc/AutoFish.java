package cn.loli.client.module.modules.misc;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.rotation.RotationHook;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class AutoFish extends Module {


    private enum MODE {
        BOUNCE("Bounce"), SPLASH("Splash") , BOTH("Both");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty mode = new EnumProperty<>("Mode", MODE.BOUNCE);

    private final BooleanProperty cast = new BooleanProperty("Cast", false);
    private final BooleanProperty move = new BooleanProperty("Auto-Move", false);

    private final BooleanProperty glitch = new BooleanProperty("Glitch", false);
    private final BooleanProperty lock = new BooleanProperty("Locked", false);

    private static final NumberProperty<Integer> role = new NumberProperty<>("Rod Handle Delay", 4500, 2000, 5000 , 500);
    private static final NumberProperty<Integer> hold = new NumberProperty<>("Catch Delay", 150, 0, 300 , 50);
    private static final NumberProperty<Integer> rerole = new NumberProperty<>("Re Cast Delay", 150, 0, 500 , 50);

    private final TimeHelper timer = new TimeHelper();
    private boolean shouldCatch = false;
    private boolean shouldReCast = false;

    float yaw, pitch;

    public AutoFish() {
        super("Auto Fish", "Auto catch the fish", ModuleCategory.MISC);
    }

    private final IEventListener<PacketEvent> onPacket = e ->
    {
        if (e.getPacket() instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect packet = (S29PacketSoundEffect) e.getPacket();
            if (!shouldReCast && packet.getSoundName().equals("random.splash")
                    && !mode.getPropertyValue().toString().equals("Bounce")) {
                shouldCatch = true;
                timer.reset();
            }
        }
    };


    private final IEventListener<MotionUpdateEvent> onMove = e ->
    {
        if (e.getEventType() == EventType.PRE) {
            if (mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                if (move.getPropertyValue())
                    if (mc.thePlayer.ticksExisted % 2 == 0)
                        moveUtils.addMotion(0.18, RotationHook.yaw + 90);
                    else
                        moveUtils.addMotion(-0.18, RotationHook.yaw + 90);

                if (glitch.getPropertyValue()) {
                    e.setX(e.getX() + mc.thePlayer.ticksExisted % 2 == 0 ? 1 : -1 * playerUtils.randomInRange(0.01, 0.02));
                    e.setZ(e.getZ() + mc.thePlayer.ticksExisted % 2 == 0 ? 1 : -1 * playerUtils.randomInRange(0.01, 0.02));
                }
                if (lock.getPropertyValue()) {
                    e.setYaw((float) (yaw * playerUtils.randomInRange(0.99, 1.01)));
                    e.setPitch((float) (pitch * playerUtils.randomInRange(0.99, 1.01)));
                }
            }
        }
    };

    private final IEventListener<TickEvent> onTick = e ->
    {
        if (mc.thePlayer.getHeldItem().getItem() != Items.fishing_rod) {
            timer.reset();
            shouldCatch = false;
            shouldReCast = false;
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
            return;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
        }

        if (mc.thePlayer.fishEntity == null) {
            if (shouldReCast) {
                if (timer.hasReached(rerole.getPropertyValue())) {
                    ((IAccessorMinecraft) mc).invokeRightClickMouse();
                    timer.reset();
                    shouldCatch = false;
                    shouldReCast = false;
                }
            } else if (cast.getPropertyValue() && timer.hasReached(role.getPropertyValue())) {
                ((IAccessorMinecraft) mc).invokeRightClickMouse();
                timer.reset();
                shouldCatch = false;
                shouldReCast = false;
            }
        } else if (staticCheck() && waterCheck()) {
            if (shouldCatch) {
                if (timer.hasReached(hold.getPropertyValue())) {
                    ((IAccessorMinecraft) mc).invokeRightClickMouse();
                    timer.reset();
                    shouldCatch = false;
                    shouldReCast = true;
                }
            } else {
                if (!mode.getPropertyValue().toString().equals("Splash")
                        && bounceCheck()) {
                    timer.reset();
                    shouldCatch = true;
                    shouldReCast = false;
                }
            }
        } else if (staticCheck()) {
            ((IAccessorMinecraft) mc).invokeRightClickMouse();
            timer.reset();
            shouldCatch = false;
            shouldReCast = false;
        }
    };


    private boolean bounceCheck() {
        if (mc.thePlayer.fishEntity == null || !waterCheck()) return false;
        return Math.abs(mc.thePlayer.fishEntity.motionY) > 0.05;
    }

    private boolean staticCheck() {
        if (mc.thePlayer.fishEntity == null || mc.thePlayer.fishEntity.isAirBorne || shouldReCast) return false;
        return Math.abs(mc.thePlayer.fishEntity.motionX) + Math.abs(mc.thePlayer.fishEntity.motionZ) < 0.01;
    }

    private boolean waterCheck() {
        if (mc.thePlayer.fishEntity == null || mc.thePlayer.fishEntity.isAirBorne) return false;
        BlockPos pos = mc.thePlayer.fishEntity.getPosition();
        return mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid || mc.theWorld.getBlockState(pos.down()).getBlock() instanceof BlockLiquid;
    }

}
