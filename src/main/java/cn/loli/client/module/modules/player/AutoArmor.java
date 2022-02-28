package cn.loli.client.module.modules.player;

import cn.loli.client.events.GuiHandleEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.InventoryUtil;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AutoArmor extends Module {

    private final List<ItemArmor> helmet;
    private final List<ItemArmor> chest;
    private final List<ItemArmor> legging;
    private final List<ItemArmor> boot;

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper throwTimer = new TimeHelper();
    final InventoryUtil inventoryUtil = InventoryUtil.getInstance();

    private final BooleanValue inv = new BooleanValue("On Inventory", false);
    private static final NumberValue<Integer> startDelay = new NumberValue<>("Start Delay", 0, 0, 200);
    private static final NumberValue<Integer> throwDelay = new NumberValue<>("Throw Delay", 0, 0, 300);

    public AutoArmor() {
        super("Auto Armor", "Make you automatically put the armor on", ModuleCategory.PLAYER);
        helmet = Arrays.asList(Items.leather_helmet, Items.golden_helmet, Items.chainmail_helmet, Items.iron_helmet, Items.diamond_helmet);
        chest = Arrays.asList(Items.leather_chestplate, Items.golden_chestplate, Items.chainmail_chestplate, Items.iron_chestplate, Items.diamond_chestplate);
        legging = Arrays.asList(Items.leather_leggings, Items.golden_leggings, Items.chainmail_leggings, Items.iron_leggings, Items.diamond_leggings);
        boot = Arrays.asList(Items.leather_boots, Items.golden_boots, Items.chainmail_boots, Items.iron_boots, Items.diamond_boots);
    }

    @EventTarget
    private void onGui(TickEvent event) {
        if (mc.currentScreen instanceof GuiInventory) {
            if (!timeHelper.hasReached((long) (startDelay.getObject() + inventoryUtil.getRandomGaussian(20)))) {
                throwTimer.reset();
                return;
            }
        } else {
            timeHelper.reset();
            if (inv.getObject())
                return;
        }

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                double random = throwDelay.getObject() == 0 ? 0 : inventoryUtil.getRandomGaussian(20);
                if (throwTimer.hasReached((long) (throwDelay.getObject() + random))) {
                    if (is.getItem() instanceof ItemArmor && isTrashArmor(is)) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 1, 4, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && helmet.contains(is.getItem()) && is == bestHelmet() && !mc.thePlayer.inventoryContainer.getSlot(5).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && chest.contains(is.getItem()) && is == bestChestplate() && !mc.thePlayer.inventoryContainer.getSlot(6).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && legging.contains(is.getItem()) && is == bestLeggings() && !mc.thePlayer.inventoryContainer.getSlot(7).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    } else if (is.getItem() instanceof ItemArmor && boot.contains(is.getItem()) && is == bestBoots() && !mc.thePlayer.inventoryContainer.getSlot(8).getHasStack()) {
                        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 0, 1, mc.thePlayer);
                        throwTimer.reset();
                        if (throwDelay.getObject() != 0) {
                            break;
                        }
                    }
                }
            }
        }
    }


    public boolean isTrashArmor(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemArmor && helmet.contains(itemStack.getItem()) && itemStack != bestHelmet() && bestHelmet() != null)
            return true;
        if (itemStack.getItem() instanceof ItemArmor && chest.contains(itemStack.getItem()) && itemStack != bestChestplate() && bestChestplate() != null)
            return true;
        if (itemStack.getItem() instanceof ItemArmor && legging.contains(itemStack.getItem()) && itemStack != bestLeggings() && bestLeggings() != null)
            return true;
        if (itemStack.getItem() instanceof ItemArmor && boot.contains(itemStack.getItem()) && itemStack != bestBoots() && bestBoots() != null)
            return true;
        return false;
    }

    public ItemStack bestHelmet() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && helmet.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }
        return bestArmor;
    }

    public ItemStack bestChestplate() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && chest.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }

        return bestArmor;
    }

    public ItemStack bestLeggings() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && legging.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }

        return bestArmor;
    }

    public ItemStack bestBoots() {
        ItemStack bestArmor = null;
        float armorSkill = -1;

        for (int i = 5; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemArmor && boot.contains(is.getItem())) {
                    float armorStrength = getFinalArmorStrength(is);
                    if (armorStrength >= armorSkill) {
                        armorSkill = getFinalArmorStrength(is);
                        bestArmor = is;
                    }
                }
            }
        }

        return bestArmor;
    }

    public float getFinalArmorStrength(ItemStack itemStack) {
        float damage = getArmorRating(itemStack);
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * 1.25F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack) * 1.20F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, itemStack) * 1.20F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack) * 1.20F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack) * 0.33F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack) * 0.10F;
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack) * 0.05F;
        return damage;
    }

    public float getArmorRating(ItemStack itemStack) {
        float rating = 0;

        if (itemStack.getItem() instanceof ItemArmor) {
            final ItemArmor armor = (ItemArmor) itemStack.getItem();
            switch (armor.getArmorMaterial()) {
                case LEATHER:
                    rating = 1;
                    break;
                case GOLD:
                    rating = 2;
                    break;
                case CHAIN:
                    rating = 3;
                    break;
                case IRON:
                    rating = 4;
                    break;
                case DIAMOND:
                    rating = 5;
                    break;
            }
        }
        return rating;
    }
}
