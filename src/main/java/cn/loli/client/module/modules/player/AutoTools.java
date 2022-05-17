package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.InventoryUtil;

import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoTools extends Module {

    private final BooleanProperty sword = new BooleanProperty("Sword-Swap", false);
    private final BooleanProperty mouseCheck = new BooleanProperty("Hold-Check", false);


    public AutoTools() {
        super("AutoTools", "Auto swap the item that you need", ModuleCategory.PLAYER);
    }


    public Entity getItems(double range) {
        Entity tempEntity = null;
        double dist = range;
        for (Entity i : mc.theWorld.loadedEntityList) {
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && (i instanceof EntityItem)) {
                double curDist = mc.thePlayer.getDistanceToEntity(i);
                if (curDist <= dist) {
                    dist = curDist;
                    tempEntity = i;
                }
            }
        }

        return tempEntity;
    }

    private final IEventListener<PacketEvent> onAttack = e ->
    {
        if ((e.getPacket() instanceof C02PacketUseEntity)
                && ((C02PacketUseEntity) e.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
            boolean checks = !mc.thePlayer.isEating();
            if (checks && sword.getPropertyValue())
                bestSword();
        }

        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packetPlayerDigging = (C07PacketPlayerDigging) e.getPacket();
            if ((packetPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                if ((mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK || !mouseCheck.getPropertyValue()) && !mc.thePlayer.capabilities.isCreativeMode) {
                    BlockPos blockPosHit = mouseCheck.getPropertyValue() ? mc.objectMouseOver.getBlockPos() : packetPlayerDigging.getPosition();
                    if (blockPosHit != null || !mouseCheck.getPropertyValue()) {
                        mc.thePlayer.inventory.currentItem = getBestTool(blockPosHit);
                        mc.playerController.updateController();
                    }
                }
            }
        }
    };





    public void bestSword() {
        int bestSlot = 0;
        double f = -1;
        for (int i1 = 36; i1 < 45; i1++) {
            if (mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null) {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if (curSlot != null && (curSlot.getItem() instanceof ItemSword)) {
                    double dmg = ((AttributeModifier) curSlot.getAttributeModifiers().get("generic.attackDamage").toArray()[0]).getAmount()
                            + inventoryUtil.getEnchantment(curSlot, Enchantment.sharpness) * 1.25
                            + inventoryUtil.getEnchantment(curSlot, Enchantment.fireAspect);
                    if (dmg > f) {
                        bestSlot = i1 - 36;
                        f = dmg;
                    }
                }
            }
        }

        if (f > -1) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }


    private int getBestTool(BlockPos pos) {
        final Block block = mc.theWorld.getBlockState(pos).getBlock();
        int slot = 0;
        float dmg = 0.1F;
        for (int index = 36; index < 45; index++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer
                    .getSlot(index).getStack();
            if (itemStack != null
                    && block != null
                    && itemStack.getItem().getStrVsBlock(itemStack, block) > dmg) {
                slot = index - 36;
                dmg = itemStack.getItem().getStrVsBlock(itemStack, block);
            }
        }
        if (dmg > 0.1F) {
            return slot;
        }
        return mc.thePlayer.inventory.currentItem;
    }

}
