package cn.loli.client.module.modules.player;

import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.*;

import java.util.Arrays;
import java.util.List;

public class InvCleaner extends Module {

    private final List<Item> trashItems;

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper throwTimer = new TimeHelper();

    private final BooleanValue openedInventory = new BooleanValue("On Inventory", true);

    private static final NumberValue<Integer> startDelay = new NumberValue<>("Start Delay", 250, 0, 500);

    private static final NumberValue<Integer> throwDelay = new NumberValue<>("Throw Delay", 100, 0, 500);

    private final BooleanValue preferSword = new BooleanValue("Prefer Sword", true);

    private final BooleanValue keepTools = new BooleanValue("Keep Tools", true);

    private static final NumberValue<Integer> swordSlot = new NumberValue<>("Sword Slot", 1, 0, 9);

    private static final NumberValue<Integer> bowSlot = new NumberValue<>("Bow Slot", 2, 0, 9);

    private static final NumberValue<Integer> pickSlot = new NumberValue<>("PickAxe Slot", 3, 0, 9);

    private static final NumberValue<Integer> axeSlot = new NumberValue<>("Axe Slot", 4, 0, 9);

    private static final NumberValue<Integer> shovelSlot = new NumberValue<>("Shovel Slot", 9, 0, 9);


    public InvCleaner() {
        super("Inv Cleaner", "Its sort the inventory for you", ModuleCategory.PLAYER);
        trashItems = Arrays.asList(Items.dye, Items.paper, Items.saddle, Items.string, Items.banner);
    }

    @EventTarget
    private void onGui(TickEvent event) {
        if (mc.currentScreen instanceof GuiInventory) {
            if (!timeHelper.hasReached((long) (startDelay.getObject() + playerUtils.getRandomGaussian(20)))) {
                throwTimer.reset();
                return;
            }
        } else {
            timeHelper.reset();
            if (openedInventory.getObject())
                return;
        }

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                double random = throwDelay.getObject() == 0 ? 0 : playerUtils.getRandomGaussian(20);
                if (throwTimer.hasReached((long) (throwDelay.getObject() + random))) {
                    if (swordSlot.getObject() != 0 && (is.getItem() instanceof ItemSword || is.getItem() instanceof ItemAxe || is.getItem() instanceof ItemPickaxe) && is == bestWeapon() && mc.thePlayer.inventoryContainer.getInventory().contains(bestWeapon()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + swordSlot.getObject())).getStack() != is && !preferSword.getObject()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (swordSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (swordSlot.getObject() != 0 && is.getItem() instanceof ItemSword && is == bestSword() && mc.thePlayer.inventoryContainer.getInventory().contains(bestSword()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + swordSlot.getObject())).getStack() != is && preferSword.getObject()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (swordSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (bowSlot.getObject() != 0 && is.getItem() instanceof ItemBow && is == bestBow() && mc.thePlayer.inventoryContainer.getInventory().contains(bestBow()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + bowSlot.getObject())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (bowSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (pickSlot.getObject() != 0 && is.getItem() instanceof ItemPickaxe && is == bestPick() && is != bestWeapon() && keepTools.getObject() && mc.thePlayer.inventoryContainer.getInventory().contains(bestPick()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + pickSlot.getObject())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (pickSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (axeSlot.getObject() != 0 && is.getItem() instanceof ItemAxe && is == bestAxe() && is != bestWeapon() && keepTools.getObject() && mc.thePlayer.inventoryContainer.getInventory().contains(bestAxe()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + axeSlot.getObject())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (axeSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (shovelSlot.getObject() != 0 && is.getItem() instanceof ItemSpade && is == bestShovel() && is != bestWeapon() && keepTools.getObject() && mc.thePlayer.inventoryContainer.getInventory().contains(bestShovel()) && mc.thePlayer.inventoryContainer.getSlot((int) (35 + shovelSlot.getObject())).getStack() != is) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, (int) (shovelSlot.getObject() - 1), 2, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (trashItems.contains(is.getItem()) || isBadStack(is)) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean isBadStack(ItemStack is) {
        if ((is.getItem() instanceof ItemSword) && is != bestWeapon() && !preferSword.getObject())
            return true;
        if (is.getItem() instanceof ItemSword && is != bestSword() && preferSword.getObject())
            return true;
        if (is.getItem() instanceof ItemBow && is != bestBow())
            return true;
        if (keepTools.getObject()) {
            if (is.getItem() instanceof ItemAxe && is != bestAxe() && (preferSword.getObject() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && is != bestPick() && (preferSword.getObject() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade && is != bestShovel())
                return true;
        } else {
            if (is.getItem() instanceof ItemAxe && (preferSword.getObject() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemPickaxe && (preferSword.getObject() || is != bestWeapon()))
                return true;
            if (is.getItem() instanceof ItemSpade)
                return true;
        }
        return false;
    }

    public ItemStack bestWeapon() {
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

    public ItemStack bestSword() {
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

    public ItemStack bestBow() {
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

    public ItemStack bestAxe() {
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

    public ItemStack bestPick() {
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

    public ItemStack bestShovel() {
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

    public float getToolRating(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, false);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack) * 2.00F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;
        return damage;
    }

    public float getItemDamage(ItemStack itemStack) {
        float damage = getToolMaterialRating(itemStack, true);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.01F;
        damage += (itemStack.getMaxDamage() - itemStack.getItemDamage()) * 0.000000000001F;

        if (itemStack.getItem() instanceof ItemSword)
            damage += 0.2;
        return damage;
    }

    public float getBowDamage(ItemStack itemStack) {
        float damage = 5;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack) * 0.75F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack) * 0.50F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.10F;
        damage += itemStack.getMaxDamage() - itemStack.getItemDamage() * 0.001F;
        return damage;
    }

    public float getToolMaterialRating(ItemStack itemStack, boolean checkForDamage) {
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