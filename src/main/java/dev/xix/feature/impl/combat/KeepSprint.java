package dev.xix.feature.impl.combat;

import dev.xix.feature.module.AbstractTheresaModule;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import dev.xix.feature.module.TheresaModuleCategory;

public class KeepSprint extends AbstractTheresaModule {

    public final BooleanValue fake = new BooleanValue("FakeFov", true);

    public boolean modify = false;

    public KeepSprint() {
        super("KeepSprint", TheresaModuleCategory.COMBAT);
    }

}
