package cn.loli.client.module.modules.combat;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.player.InventoryUtil;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoPot extends Module {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 600, 150, 1500);
    private final NumberValue<Integer> healPercent = new NumberValue<>("HealPercent", 70, 5, 100);
    private final BooleanValue heal = new BooleanValue("Heal", true);
    private final BooleanValue speed = new BooleanValue("Speed", true);
    private final BooleanValue heads = new BooleanValue("Heads", true);


    public boolean potting;


    public AutoPot() {
        super("Auto Pot", "Auto Throw pot to heal u or do others", ModuleCategory.COMBAT);

    }

    @Override
    public void onDisable() {
    }


}
