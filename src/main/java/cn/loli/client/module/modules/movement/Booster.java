package cn.loli.client.module.modules.movement;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.NumberProperty;
import net.minecraft.init.Blocks;

public class Booster extends Module {

    private static final NumberProperty<Float> slipper = new NumberProperty<>("Slipperiness", 0.39f, 0.1f, 1f , 0.01f);

    public Booster() {
        super("Ice Boost", "You are fast with ice", ModuleCategory.MOVEMENT);
    }


    @Override
    public void onEnable() {
        Blocks.ice.slipperiness = slipper.getPropertyValue();
        Blocks.packed_ice.slipperiness = slipper.getPropertyValue();
        
    }

    @Override
    public void onDisable() {
        Blocks.ice.slipperiness = 0.98F;
        Blocks.packed_ice.slipperiness = 0.98F;
        
    }
}
