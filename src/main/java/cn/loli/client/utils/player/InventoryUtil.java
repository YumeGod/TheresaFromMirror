package cn.loli.client.utils.player;

import cn.loli.client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtil extends Utils {

    private static InventoryUtil inventoryUtil;

    public boolean hasAir() {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null) {
                return true;
            }
        }
        return false;
    }

    public int blockSize(List<Block> blacklist) {
        int blocks = 0;
        for(int i = 0;  i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if(itemStack != null && itemStack.getItem() instanceof ItemBlock && (blacklist == null || !blacklist.contains(Block.getBlockFromItem(itemStack.getItem())))) {
                blocks += itemStack.stackSize;
            }
        }
        return blocks;
    }

    public int searchFood(List<Item> whitelist) {
        int blocks = 0;
        for(int i = 0;  i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if(itemStack != null && itemStack.getItem() instanceof ItemFood && (whitelist == null || whitelist.contains(itemStack.getItem()))) {
                blocks += itemStack.stackSize;
            }
        }
        return blocks;
    }

    public int searchDamageItem(int start, int end) {
        for(int i = start; i < end; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if(itemStack != null && (itemStack.getItem() instanceof ItemBow || itemStack.getItem() instanceof ItemFishingRod || itemStack.getItem() instanceof ItemSnowball || itemStack.getItem() instanceof ItemEnderPearl)) {
                return i;
            }
        }
        return -1;
    }

    public void switchSmooth(int start, int end) {
        for(int i : calculateSwitch(start, end)) {
            mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(i));
        }
    }


    public ArrayList<Integer> calculateSwitch(int start, int end) {
        int expandedDistance = start - (end + 9);
        int normalDistance = start - end;

        int tempExpand = expandedDistance;
        int tempNormal = normalDistance;
        expandedDistance = Math.abs(expandedDistance);
        normalDistance = Math.abs(normalDistance);

        if(expandedDistance > 9) {
            expandedDistance -= 9;
        }

        int itemSwitch = Math.min(expandedDistance, normalDistance);

        final ArrayList<Integer> switches = new ArrayList<>();

        if(expandedDistance == itemSwitch) {
            if(tempExpand > 0) {
                for(int i = 0; i < tempExpand; i++)
                    switches.add(i > 9 ? i - 9 : i);
            } else {
                for(int i = 0; i > tempExpand; i--)
                    switches.add(i < 0 ? i + 9 : i);
            }
        } else {
            if(tempNormal > 0) {
                for(int i = 0; i < tempNormal; i++)
                    switches.add(i > 9 ? i - 9 : i);
            } else {
                for(int i = 0; i > tempNormal; i--)
                    switches.add(i < 0 ? i + 9 : i);
            }
        }
        return switches;
    }

    public int getUnusableSlot() {
        for(int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if(itemStack == null || itemStack.getItemUseAction().equals(EnumAction.NONE))
                return i;
        }
        return -1;
    }

    public int getItemSize(Item item, IInventory inventory) {
        int size = 0;
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            if(inventory.getStackInSlot(i) != null) {
                ItemStack stack = inventory.getStackInSlot(i);
                if(stack.getItem().equals(item) && stack.stackSize != stack.getMaxStackSize())
                    size++;
            }
        }
        return size;
    }

    public int getAir() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null) {
                return i;
            }
        }
        return -1;
    }

    public ItemStack getItem(int slot) {
        return mc.thePlayer.inventory.getStackInSlot(slot);
    }

    public ItemStack searchInHotbar(Item itm) {
        return searchItem(itm, 0, 8);
    }

    public ItemStack searchInInventory(Item itm) {
        return searchItem(itm, 9, 45);
    }

    public int searchSlotInventory(Item itm) {
        return searchItemSlot(itm, 9, 45);
    }

    public int searchSlotInHotbar(Item itm) {
        return searchItemSlot(itm, 0, 8);
    }

    public int searchItemSlot(Item itm, int begin, int end) {
        for (int i = begin; i < end; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                if (itemStack.getItem().equals(itm)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int searchItemSlot(List<Item> itemList, int begin, int end) {
        for (int i = begin; i < end; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                if (itemList.contains(itemStack.getItem())) {
                    return i;
                }
            }
        }
        return -1;
    }
    public int getEnchantment(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags())
            return 0;

        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); i++) {
            final NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);

            if ((tagCompound.hasKey("ench") && tagCompound.getShort("ench") == enchantment.effectId) || (tagCompound.hasKey("id") && tagCompound.getShort("id") == enchantment.effectId))
                return tagCompound.getShort("lvl");
        }

        return 0;
    }

    public ItemStack searchItem(Item itm, int begin, int end) {
        for (int i = begin; i < end; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null) {
                if (itemStack.getItem().equals(itm)) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    public boolean isBuffPotion(final ItemStack stack) {
        final ItemPotion potion = (ItemPotion) stack.getItem();
        final List<PotionEffect> effects = potion.getEffects(stack);

        for (final PotionEffect effect : effects)
            if (Potion.potionTypes[effect.getPotionID()].isBadEffect())
                return false;

        return true;
    }

    public static InventoryUtil getInstance() {
        if(inventoryUtil == null) {
            inventoryUtil = new InventoryUtil();
        }
        return inventoryUtil;
    }

    public abstract class WindowClickRequest {
        private boolean completed;

        public abstract void performRequest();

        public boolean isCompleted() {
            return this.completed;
        }

        public void onCompleted() {
            this.completed = true;
        }
    }
}
