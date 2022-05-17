package cn.loli.client.module.modules.combat;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;

public class AutoPot extends Module {

    private final NumberProperty<Integer> delay = new NumberProperty<>("Delay", 600, 150, 1500 , 50);
    private final NumberProperty<Integer> healPercent = new NumberProperty<>("HealPercent", 70, 5, 100 , 5);
    private final BooleanProperty heal = new BooleanProperty("Heal", true);
    private final BooleanProperty speed = new BooleanProperty("Speed", true);
    private final BooleanProperty heads = new BooleanProperty("Heads", true);


    public boolean potting;


    public AutoPot() {
        super("Auto Pot", "Auto Throw pot to heal u or do others", ModuleCategory.COMBAT);

    }

    @Override
    public void onDisable() {
    }


}
