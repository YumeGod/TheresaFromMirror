package dev.xix.feature.module.combat;

import cn.loli.client.module.modules.combat.Velocity;
import dev.xix.feature.module.AbstractTheresaModule;
import dev.xix.feature.module.TheresaModuleCategory;

public final class VelocityModule extends AbstractTheresaModule {
    private static VelocityModule module;

    public VelocityModule() {
        super("Velocity", TheresaModuleCategory.COMBAT);
        module = this;
    }


    @Override
    public VelocityModule getInstance() {
        return module;
    }
    
    public static VelocityModule getVelocity() {
        return module.getInstance();
    }
}
