package cn.loli.client.module.modules.player;

import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;

public class InvCleaner extends Module {

    private final List<Item> trashItems = Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner);

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper throwTimer = new TimeHelper();

    private final BooleanProperty openedInventory = new BooleanProperty("On Inventory", true);

    private static final NumberProperty<Integer> startDelay = new NumberProperty<>("Start Delay", 250, 0, 500 , 50);

    private static final NumberProperty<Integer> throwDelay = new NumberProperty<>("Throw Delay", 100, 0, 500 , 50);

    private final BooleanProperty preferSword = new BooleanProperty("Prefer Sword", true);

    private final BooleanProperty keepTools = new BooleanProperty("Keep Tools", true);

    private final BooleanProperty keepRod = new BooleanProperty("Keep Rods", false);

    private static final NumberProperty<Integer> swordSlot = new NumberProperty<>("Sword Slot", 1, 0, 9 , 1);

    private static final NumberProperty<Integer> bowSlot = new NumberProperty<>("Bow Slot", 2, 0, 9 , 1);

    private static final NumberProperty<Integer> pickSlot = new NumberProperty<>("PickAxe Slot", 3, 0, 9 , 1);

    private static final NumberProperty<Integer> axeSlot = new NumberProperty<>("Axe Slot", 4, 0, 9 , 1);

    private static final NumberProperty<Integer> shovelSlot = new NumberProperty<>("Shovel Slot", 9, 0, 9 , 1);


    public InvCleaner() {
        super("Inv Cleaner", "Its sort the inventory for you", ModuleCategory.PLAYER);
    }

    private final IEventListener<TickEvent> onGui = event ->
    {
        if (mc.currentScreen instanceof GuiInventory) {
            if (!timeHelper.hasReached((long) (startDelay.getPropertyValue() + playerUtils.getRandomGaussian(20)))) {
                throwTimer.reset();
                return;
            }
        } else {
            timeHelper.reset();
            if (openedInventory.getPropertyValue())
                return;
        }

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                double random = throwDelay.getPropertyValue() == 0 ? 0 : playerUtils.getRandomGaussian(20);
                if (throwTimer.hasReached((long) (throwDelay.getPropertyValue() + random))) {
                    if (swordSlot.getPropertyValue() != 0 && (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) && is == bestWeapon() && mc.thePlayer.inventoryContainer.getInventory().contains(bestWeapon()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + swordSlot.getPropertyValue())).getStack() != is && !preferSword.getPropertyValue()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (swordSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (swordSlot.getPropertyValue() != 0 && is.getItem() instanceof ItemSword && is == bestSword() && mc.thePlayer.inventoryContainer.getInventory().contains(bestSword()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + swordSlot.getPropertyValue())).getStack() != is && preferSword.getPropertyValue()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (swordSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (bowSlot.getPropertyValue() != 0 && is.getItem() instanceof ItemBow && is == bestBow() && mc.thePlayer.inventoryContainer.getInventory().contains(bestBow()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + bowSlot.getPropertyValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (bowSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (pickSlot.getPropertyValue() != 0 && is.getItem() instanceof ItemPickaxe && is == bestPick() && is != bestWeapon() && keepTools.getPropertyValue() && mc.thePlayer.inventoryContainer.getInventory().contains(bestPick()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + pickSlot.getPropertyValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (pickSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (axeSlot.getPropertyValue() != 0 && is.getItem() instanceof ItemAxe && is == bestAxe() && is != bestWeapon() && keepTools.getPropertyValue() && mc.thePlayer.inventoryContainer.getInventory().contains(bestAxe()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + axeSlot.getPropertyValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (axeSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (shovelSlot.getPropertyValue() != 0 && is.getItem() instanceof ItemSpade && is == bestShovel() && is != bestWeapon() && keepTools.getPropertyValue() && mc.thePlayer.inventoryContainer.getInventory().contains(bestShovel()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + shovelSlot.getPropertyValue())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (shovelSlot.getPropertyValue() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    } else if (trashItems.contains(is.getItem()) || isBadStack(is)) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getPropertyValue() != 0) {
                            break;
                        }
                    }
                }
            }
        }
    };


    public boolean isBadStack(ItemStack is) {
        if ((is.getItem() instanceof ItemSword) && is != bestWeapon() && !preferSword.getPropertyValue())
            return true;
        if (is.getItem() instanceof ItemSword && is != bestSword() && preferSword.getPropertyValue())
            return true;
        if (is.getItem() instanceof ItemBow && is != bestBow())
            return true;
        if (is.getItem() instanceof ItemFishingRod && !keepRod.getPropertyValue())
            return true;
        if (is.getItem().getUnlocalizedName().contains("potion"))
            return !inventoryUtil.isBuffPotion(is);

        if (keepTools.getPropertyValue()) {
            if (is.getItem() instanceof ItemAxe && is != bestAxe() && (preferSword.getPropertyValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && is != bestPick() && (preferSword.getPropertyValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade && is != bestShovel())
                return true;
        } else {
            if (is.getItem() instanceof ItemAxe && (preferSword.getPropertyValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && (preferSword.getPropertyValue() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade)
                return true;
        }
        return false;
    }


    ItemStack bestWeapon() {
        ItemStack bestWeapon = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) {
                    float toolDamage = getItemDamage(is);
                    if (toolDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestWeapon = is;
                    }
                }
            }
        }

        return bestWeapon;
    }

    ItemStack bestSword() {
        ItemStack bestSword = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSword) {
                    float swordDamage = getItemDamage(is);
                    if (swordDamage >= itemDamage) {
                        itemDamage = getItemDamage(is);
                        bestSword = is;
                    }
                }
            }
        }

        return bestSword;
    }

    ItemStack bestBow() {
        ItemStack bestBow = null;
        float itemDamage = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBow) {
                    float bowDamage = getBowDamage(is);
                    if (bowDamage >= itemDamage) {
                        itemDamage = getBowDamage(is);
                        bestBow = is;
                    }
                }
            }
        }

        return bestBow;
    }

    ItemStack bestAxe() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemAxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    ItemStack bestPick() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemPickaxe) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    ItemStack bestShovel() {
        ItemStack bestTool = null;
        float itemSkill = -1;

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemSpade) {
                    float toolSkill = getToolRating(is);
                    if (toolSkill >= itemSkill) {
                        itemSkill = getToolRating(is);
                        bestTool = is;
                    }
                }
            }
        }

        return bestTool;
    }

    float getToolRating(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, false);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.00F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;
        return damage;
    }

    float getItemDamage(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, true);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;

        if (itemStack.getItem() instanceof ItemSword)
            damage += 0.2;
        return damage;
    }

    float getBowDamage(ItemStack itemStack) {
        float damage = 5;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += itemStack.getMaxDamage() - itemStack.getItemDamage() * 0.001F;
        return damage;
    }

    float getToolMaterialRating(ItemStack itemStack, boolean checkForDamage) {
        final Item is = itemStack.getItem();
        int rating = 0;

        if (is instanceof ItemSword) {
            switch (((ItemSword) is).getToolMaterialName()) {
                case "WOOD":
                case "GOLD":
                    rating = 4;
                    break;
                case "STONE":
                    rating = 5;
                    break;
                case "IRON":
                    rating = 6;
                    break;
                case "EMERALD":
                    rating = 7;
                    break;
            }
        } else if (is instanceof ItemPickaxe) {
            switch (((ItemPickaxe) is).getToolMaterialName()) {
                case "WOOD":
                case "GOLD":
                    rating = 2;
                    break;
                case "STONE":
                    rating = 3;
                    break;
                case "IRON":
                    rating = checkForDamage ? 4 : 40;
                    break;
                case "EMERALD":
                    rating = checkForDamage ? 5 : 50;
                    break;
            }
            ;
        } else if (is instanceof ItemAxe) {
            switch (((ItemAxe) is).getToolMaterialName()) {
                case "WOOD":
                case "GOLD":
                    rating = 3;
                    break;
                case "STONE":
                    rating = 4;
                    break;
                case "IRON":
                    rating = 5;
                    break;
                case "EMERALD":
                    rating = 6;
                    break;
            }
        } else if (is instanceof ItemSpade) {
            switch (((ItemSpade) is).getToolMaterialName()) {
                case "WOOD":
                case "GOLD":
                    rating = 1;
                    break;
                case "STONE":
                    rating = 2;
                    break;
                case "IRON":
                    rating = 3;
                    break;
                case "EMERALD":
                    rating = 4;
                    break;
            }
            ;
        }

        return rating;
    }
}
