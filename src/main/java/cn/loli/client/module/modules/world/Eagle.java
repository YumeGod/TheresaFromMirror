

package cn.loli.client.module.modules.world;

import cn.loli.client.events.RenderEvent;
import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class Eagle extends Module {
    private final BooleanProperty onlyBlocks = new BooleanProperty("OnlyBlocks", true);
    private final BooleanProperty onlyLookingDown = new BooleanProperty("OnlyLookingDown", false);

    private boolean resetFlag = true;

    public Eagle() {
        super("Eagle", "Safewalk but with sneaking. Useful for 'Eagle Bridging'", ModuleCategory.WORLD);
    }

    private final IEventListener<UpdateEvent> onUpdate = event -> {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();
            BlockPos belowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
            if ((!onlyBlocks.getPropertyValue() || (heldItem != null && heldItem.getItem() instanceof ItemBlock))
                    && (!onlyLookingDown.getPropertyValue() || mc.thePlayer.rotationPitch > 45)
                    && mc.thePlayer.onGround) {

                ((IAccessorKeyBinding) mc.gameSettings.keyBindSneak).setPressed(false);

                if (mc.theWorld.getBlockState(belowPlayer).getBlock() == Blocks.air) {
                    ((IAccessorKeyBinding) mc.gameSettings.keyBindSneak).setPressed(true);
                }

                resetFlag = false;
            } else {
                if (!resetFlag) {
                    ((IAccessorKeyBinding) mc.gameSettings.keyBindSneak).setPressed(false);
                    resetFlag = true;
                }
            }
        }
    };


}
