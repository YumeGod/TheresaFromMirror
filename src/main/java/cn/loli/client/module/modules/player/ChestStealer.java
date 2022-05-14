package cn.loli.client.module.modules.player;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestStealer extends Module {

    private final TimeHelper timeHelper = new TimeHelper();
    private final TimeHelper startTimer = new TimeHelper();
    private final List<Integer> itemsToSteal = new ArrayList<>();

    private static final NumberProperty<Integer> startDelay = new NumberProperty<>("Start Delay", 0, 0, 200 , 10);
    private static final NumberProperty<Integer> pickDelay = new NumberProperty<>("Pick Delay", 0, 0, 300 , 10);

    private final BooleanProperty intelligent = new BooleanProperty("Pick Useful", false);
    private final BooleanProperty stackItems = new BooleanProperty("Stack Items", false);
    private final BooleanProperty randomPick = new BooleanProperty("Pick Randomize", false);
    private final BooleanProperty autoClose = new BooleanProperty("Auto Close", false);
    boolean isChest = false;

    public ChestStealer() {
        super("Chest Stealer", "You steal the items from a chest", ModuleCategory.PLAYER);
    }

    private final IEventListener<TickEvent> onGui = event ->
    {
        if (mc.currentScreen == null) {
            timeHelper.reset();
            startTimer.reset();
            isChest = false;
            return;
        }

        if (mc.currentScreen instanceof GuiChest) {

            if (!startTimer.hasReached((long) (startDelay.getPropertyValue() + inventoryUtil.getRandomGaussian(20))))
                return;

            itemsToSteal.clear();

            final ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
            final IInventory inventory = chest.getLowerChestInventory();
            boolean isEmpty = true;

            final String chestName = inventory.getDisplayName().getUnformattedText();

            if (!chestName.equals(I18n.format("container.chest")) && !chestName.equals(I18n.format("container.chestDouble")))
                return;

            if (intelligent.getPropertyValue()) {
                addIntelligentSlotsToSteal();
            } else {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    final ItemStack stack = inventory.getStackInSlot(i);
                    if (stack != null) {
                        itemsToSteal.add(i);
                    }
                }
            }

            if (randomPick.getPropertyValue())
                Collections.shuffle(itemsToSteal);

            for (final int i : itemsToSteal) {
                final ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null) {
                    double random = pickDelay.getPropertyValue() == 0 ? 0 : inventoryUtil.getRandomGaussian(20);
                    if (!timeHelper.hasReached((long) (pickDelay.getPropertyValue() + random)))
                        return;
                    if (stackItems.getPropertyValue() && stack.stackSize != 64 && stack.getMaxStackSize() != 1 && inventoryUtil.getItemSize(stack.getItem(), inventory) != 0 && inventoryUtil.getItemSize(stack.getItem(), inventory) != 1) {
                        mc.playerController.windowClick(chest.windowId, i, 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(chest.windowId, i, 0, 6, mc.thePlayer);
                        mc.playerController.windowClick(chest.windowId, i, 0, 0, mc.thePlayer);
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                    } else {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                    }
                    timeHelper.reset();
                    isEmpty = false;
                }
            }

            if (isEmpty && autoClose.getPropertyValue())
                mc.thePlayer.closeScreen();
        }
    };

    private final IEventListener<PacketEvent> onCheck = event ->
    {
        final Packet<?> packet = event.getPacket();

        // Open delay (wait before you steal)
        if (packet instanceof S2DPacketOpenWindow) { // When you open a window
            final S2DPacketOpenWindow openWindow = (S2DPacketOpenWindow) packet;

            if (openWindow.getGuiId().equals("minecraft:container"))
                isChest = false;

        }
    };


    private void addIntelligentSlotsToSteal() {
        float bestSwordDamage = -1, bestBowDamage = -1, bestPickAxeStrength = -1, bestAxeStrength = -1;
        float bestBootsProtection = -1, bestLeggingsProtection = -1, bestChestPlateProtection = -1, bestHelmetProtection = -1;
        //searching in inventory
        for (int i = 9; i < 45; i++) {
            final Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot != null && slot.getStack() != null) {
                final ItemStack stack = slot.getStack();
                if (stack != null) {
                    final Item item = stack.getItem();
                    if (item instanceof ItemSword) {
                        final float damage = 4 + ((ItemSword) stack.getItem()).getDamageVsEntity() + (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f);
                        if (damage > bestSwordDamage) {
                            bestSwordDamage = damage;
                        }
                    } else if (item instanceof ItemBow) {
                        bestBowDamage = 4.5f;
                    } else if (item instanceof ItemTool) {
                        Block block = null;
                        if (item instanceof ItemPickaxe) {
                            block = Blocks.stone;
                        } else if (item instanceof ItemAxe) {
                            block = Blocks.log;
                        }
                        final float toolStrength = item.getStrVsBlock(stack, block);
                        if (item instanceof ItemPickaxe) {
                            if (toolStrength > bestPickAxeStrength) {
                                bestPickAxeStrength = toolStrength;
                            }
                        } else if (item instanceof ItemAxe) {
                            if (toolStrength > bestAxeStrength) {
                                bestAxeStrength = toolStrength;
                            }
                        }
                    } else if (item instanceof ItemArmor) {
                        final float prot = ((ItemArmor) stack.getItem()).damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.04f;
                        switch (((ItemArmor) item).armorType) {
                            case 0:
                                if (prot > bestHelmetProtection) {
                                    bestHelmetProtection = prot;
                                }
                                break;
                            case 1:
                                if (prot > bestChestPlateProtection) {
                                    bestChestPlateProtection = prot;
                                }
                                break;
                            case 2:
                                if (prot > bestLeggingsProtection) {
                                    bestLeggingsProtection = prot;
                                }
                                break;
                            case 3:
                                if (prot > bestBootsProtection) {
                                    bestBootsProtection = prot;
                                }
                                break;
                        }
                    }
                }
            }
        }

        int bestSwordSlot = -1, bestBowSlot = -1, bestPickAxeSlot = -1, bestAxeSlot = -1, bestBootsSlot = -1, bestLeggingsSlot = -1, bestChestPlateSlot = -1, bestHelmetSlot = -1;
        for (int i = 0; i < ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getSizeInventory(); i++) {
            final ItemStack stack = mc.thePlayer.openContainer.getSlot(i).getStack();
            if (stack != null) {
                final Item item = mc.thePlayer.openContainer.getSlot(i).getStack().getItem();
                if (item instanceof ItemSword) {
                    final float damage = 4 + ((ItemSword) stack.getItem()).getDamageVsEntity() + (EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f);
                    if (damage > bestSwordDamage) {
                        bestSwordDamage = damage;
                        bestSwordSlot = i;
                    }
                } else if (item instanceof ItemBow) {
                    if (4.5f > bestBowDamage) {
                        bestBowDamage = 4.5f;
                        bestBowSlot = i;
                    }
                } else if (item instanceof ItemTool) {
                    Block block = null;
                    if (item instanceof ItemPickaxe) {
                        block = Blocks.stone;
                    } else if (item instanceof ItemAxe) {
                        block = Blocks.log;
                    }
                    final float toolStrength = item.getStrVsBlock(stack, block);
                    if (item instanceof ItemPickaxe) {
                        if (toolStrength > bestPickAxeStrength) {
                            bestPickAxeStrength = toolStrength;
                            bestPickAxeSlot = i;
                        }
                    } else if (item instanceof ItemAxe) {
                        if (toolStrength > bestAxeStrength) {
                            bestAxeStrength = toolStrength;
                            bestAxeSlot = i;
                        }
                    }
                } else if (item instanceof ItemArmor) {
                    final float prot = ((ItemArmor) stack.getItem()).damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.04f;
                    switch (((ItemArmor) item).armorType) {
                        case 0:
                            if (prot > bestHelmetProtection) {
                                bestHelmetProtection = prot;
                                bestHelmetSlot = i;
                            }
                            break;
                        case 1:
                            if (prot > bestChestPlateProtection) {
                                bestChestPlateProtection = prot;
                                bestChestPlateSlot = i;
                            }
                            break;
                        case 2:
                            if (prot > bestLeggingsProtection) {
                                bestLeggingsProtection = prot;
                                bestLeggingsSlot = i;
                            }
                            break;
                        case 3:
                            if (prot > bestBootsProtection) {
                                bestBootsProtection = prot;
                                bestBootsSlot = i;
                            }
                            break;
                    }
                } else {
                    itemsToSteal.add(i);
                }
            }
        }
        itemsToSteal.add(bestSwordSlot);
        itemsToSteal.add(bestBowSlot);
        itemsToSteal.add(bestPickAxeSlot);
        itemsToSteal.add(bestAxeSlot);
        itemsToSteal.add(bestHelmetSlot);
        itemsToSteal.add(bestChestPlateSlot);
        itemsToSteal.add(bestLeggingsSlot);
        itemsToSteal.add(bestBootsSlot);
    }
}
