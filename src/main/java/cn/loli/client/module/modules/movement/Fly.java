

package cn.loli.client.module.modules.movement;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;

public class Fly extends Module {
    private final ModeValue mode = new ModeValue("Mode", "Vanilla", "Vanilla");

    public Fly() {
        super("Fly", "Reach for the skies!", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = true;

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = true;
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (mc.thePlayer == null) return;
        if (mode.getCurrentMode().equalsIgnoreCase("Vanilla") && !mc.thePlayer.isSpectator()) {
            mc.thePlayer.capabilities.isFlying = false;
            mc.thePlayer.capabilities.setFlySpeed(0.05f);

            if (!mc.thePlayer.capabilities.isCreativeMode)
                mc.thePlayer.capabilities.allowFlying = false;
        }
    }

//    @EventTarget
//    public void onMove(MotionUpdateEvent event) {
//        if (mc.thePlayer == null) return;
//
//        // TODO: More fly modes
//    }
}
