package cn.loli.client.module.modules.movement;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import net.minecraft.init.Blocks;

public class Booster extends Module {

    private static final NumberValue<Float> slipper = new NumberValue<>("Slipperiness", 0.39f, 0.1f, 1f);

    public Booster() {
        super("Ice Boost", "You are fast with ice", ModuleCategory.MOVEMENT);
    }


    @Override
    public void onEnable() {
        Blocks.ice.slipperiness = slipper.getObject();
        Blocks.packed_ice.slipperiness = slipper.getObject();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
        super.onDisable();
    }
}
