

package cn.loli.client.module.modules.player;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;

public class NoRightClickDelay extends Module {
    public NoRightClickDelay() {
        super("NoRightClickDelay", "Removes the right click delay.", ModuleCategory.PLAYER);
    }

    /**
     * Credit: CPS Cap Bypass Mod by caterpillow
     */
    @EventTarget
    public void onUpdate(UpdateEvent e) {
        if (mc.gameSettings.keyBindUseItem.isKeyDown() && !mc.thePlayer.isUsingItem()) {
            ((IAccessorMinecraft) mc).invokeRightClickMouse();
        }
    }
}
