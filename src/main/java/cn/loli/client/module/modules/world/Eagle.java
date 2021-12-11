

package cn.loli.client.module.modules.world;

import cn.loli.client.injection.mixins.IAccessorKeyBinding;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public class Eagle extends Module {
    private final BooleanValue onlyBlocks = new BooleanValue("OnlyBlocks", true);
    private final BooleanValue onlyLookingDown = new BooleanValue("OnlyLookingDown", false);

    private boolean resetFlag = true;

    public Eagle() {
        super("Eagle", "Safewalk but with sneaking. Useful for 'Eagle Bridging'", ModuleCategory.WORLD);
    }

    @EventTarget
    public void onUpdate(UpdateEvent e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();
            BlockPos belowPlayer = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
            if ((!onlyBlocks.getObject() || (heldItem != null && heldItem.getItem() instanceof ItemBlock))
                    && (!onlyLookingDown.getObject() || mc.thePlayer.rotationPitch > 45)
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
    }
}
