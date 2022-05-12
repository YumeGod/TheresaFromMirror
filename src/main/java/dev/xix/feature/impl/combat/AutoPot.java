package dev.xix.feature.impl.combat;

import dev.xix.feature.module.AbstractTheresaModule;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import dev.xix.feature.module.TheresaModuleCategory;

public class AutoPot extends AbstractTheresaModule {

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 600, 150, 1500);
    private final NumberValue<Integer> healPercent = new NumberValue<>("HealPercent", 70, 5, 100);
    private final BooleanValue heal = new BooleanValue("Heal", true);
    private final BooleanValue speed = new BooleanValue("Speed", true);
    private final BooleanValue heads = new BooleanValue("Heads", true);


    public boolean potting;


    public AutoPot() {
        super("Auto Pot", TheresaModuleCategory.COMBAT);

    }

    @Override
    public void onDisable() {
    }


}
