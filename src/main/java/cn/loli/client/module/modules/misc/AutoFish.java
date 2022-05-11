package cn.loli.client.module.modules.misc;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.rotation.RotationHook;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;

import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class AutoFish extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Bounce", "Bounce", "Splash", "Both");

    private final BooleanValue cast = new BooleanValue("Cast", false);
    private final BooleanValue move = new BooleanValue("Auto-Move", false);

    private final BooleanValue glitch = new BooleanValue("Glitch", false);
    private final BooleanValue lock = new BooleanValue("Locked", false);

    private static final NumberValue<Integer> role = new NumberValue<>("Rod Handle Delay", 4500, 2000, 5000);
    private static final NumberValue<Integer> hold = new NumberValue<>("Catch Delay", 150, 0, 300);
    private static final NumberValue<Integer> rerole = new NumberValue<>("Re Cast Delay", 150, 0, 500);

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
                    && !mode.getCurrentMode().equalsIgnoreCase("Bounce")) {
                shouldCatch = true;
                timer.reset();
            }
        }
    };


    private final IEventListener<MotionUpdateEvent> onMove = e ->
    {
        if (e.getEventType() == EventType.PRE) {
            if (mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                if (move.getObject())
                    if (mc.thePlayer.ticksExisted % 2 == 0)
                        moveUtils.addMotion(0.18, RotationHook.yaw + 90);
                    else
                        moveUtils.addMotion(-0.18, RotationHook.yaw + 90);

                if (glitch.getObject()) {
                    e.setX(e.getX() + mc.thePlayer.ticksExisted % 2 == 0 ? 1 : -1 * playerUtils.randomInRange(0.01, 0.02));
                    e.setZ(e.getZ() + mc.thePlayer.ticksExisted % 2 == 0 ? 1 : -1 * playerUtils.randomInRange(0.01, 0.02));
                }
                if (lock.getObject()) {
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
                if (timer.hasReached(rerole.getObject())) {
                    ((IAccessorMinecraft) mc).invokeRightClickMouse();
                    timer.reset();
                    shouldCatch = false;
                    shouldReCast = false;
                }
            } else if (cast.getObject() && timer.hasReached(role.getObject())) {
                ((IAccessorMinecraft) mc).invokeRightClickMouse();
                timer.reset();
                shouldCatch = false;
                shouldReCast = false;
            }
        } else if (staticCheck() && waterCheck()) {
            if (shouldCatch) {
                if (timer.hasReached(hold.getObject())) {
                    ((IAccessorMinecraft) mc).invokeRightClickMouse();
                    timer.reset();
                    shouldCatch = false;
                    shouldReCast = true;
                }
            } else {
                if (!mode.getCurrentMode().equalsIgnoreCase("Splash")
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
