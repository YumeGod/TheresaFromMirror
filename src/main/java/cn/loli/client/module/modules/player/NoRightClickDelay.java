

package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;

public class NoRightClickDelay extends Module {
    public NoRightClickDelay() {
        super("NoRightClickDelay", "Removes the right click delay.", ModuleCategory.PLAYER);
    }

    /**
     * Credit: CPS Cap Bypass Mod by caterpillow
     */
    private final IEventListener<UpdateEvent> onUpdate = event ->
    {
        if (mc.gameSettings.keyBindUseItem.isKeyDown() && (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))
            ((IAccessorMinecraft) mc).invokeRightClickMouse();
    };


}
