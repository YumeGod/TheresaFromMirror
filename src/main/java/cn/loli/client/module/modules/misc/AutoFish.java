package cn.loli.client.module.modules.misc;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class AutoFish extends Module {

    private final ModeValue mode = new ModeValue("Mode", "Bounce", "Bounce", "Splash", "Both");

    private final BooleanValue cast = new BooleanValue("Cast", false);
    private final BooleanValue move = new BooleanValue("Auto-Move", false);
    private final BooleanValue lock = new BooleanValue("Locked", false);

    private final TimeHelper timer = new TimeHelper();
    private boolean shouldCatch = false;
    private boolean shouldReCast = false;

    float yaw, pitch;

    public AutoFish() {
        super("Auto Fish", "Auto catch the fish", ModuleCategory.MISC);
    }

    @EventTarget
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof S29PacketSoundEffect) {
            S29PacketSoundEffect packet = (S29PacketSoundEffect) e.getPacket();
            if (!shouldReCast && staticCheck() && packet.getSoundName().equals("random.splash") && !mode.getCurrentMode().equalsIgnoreCase("Bounce")) {
                shouldCatch = true;
                timer.reset();
            }
        }
    }


    @EventTarget
    public void onMove(MotionUpdateEvent e) {
        if (e.getEventType() == EventType.PRE) {
            if (mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) {
                if (move.getObject()) {
                    e.setX(e.getX() + playerUtils.randomInRange(-0.049, 0.049));
                    e.setZ(e.getZ() - playerUtils.randomInRange(-0.049, 0.049));
                }
                if (lock.getObject()) {
                    e.setYaw((float) (yaw * playerUtils.randomInRange(0.98, 1.02)));
                    e.setPitch(pitch);
                }
            }
        }
    }

    @EventTarget
    public void onTick(TickEvent e) {
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
                if (timer.hasReached(450)) {
                    ((IAccessorMinecraft) mc).invokeRightClickMouse();
                    timer.reset();
                    shouldCatch = false;
                    shouldReCast = false;
                }
            } else if (cast.getObject() && timer.hasReached(4500)) {
                ((IAccessorMinecraft) mc).invokeRightClickMouse();
                timer.reset();
                shouldCatch = false;
                shouldReCast = false;
            }
        } else if (staticCheck() && waterCheck()) {
            if (shouldCatch) {
                if (timer.hasReached(350)) {
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
    }


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
