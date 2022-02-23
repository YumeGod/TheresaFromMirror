package cn.loli.client.utils.player;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InvUtils {

    public static int getEnchantment(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack == null || itemStack.getEnchantmentTagList() == null || itemStack.getEnchantmentTagList().hasNoTags())
            return 0;

        for (int i = 0; i < itemStack.getEnchantmentTagList().tagCount(); i++) {
            final NBTTagCompound tagCompound = itemStack.getEnchantmentTagList().getCompoundTagAt(i);

            if ((tagCompound.hasKey("ench") && tagCompound.getShort("ench") == enchantment.effectId) || (tagCompound.hasKey("id") && tagCompound.getShort("id") == enchantment.effectId))
                return tagCompound.getShort("lvl");
        }

        return 0;
    }

}
