package dev.xix.feature.module.combat;

import cn.loli.client.module.modules.combat.Velocity;
import dev.xix.TheresaClient;
import dev.xix.event.Event;
import dev.xix.event.EventCancellable;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.AbstractTheresaModule;
import dev.xix.feature.module.TheresaModuleCategory;
import dev.xix.feature.module.TheresaModuleManager;

public final class VelocityModule extends AbstractTheresaModule {
    public VelocityModule() {
        super("Velocity", TheresaModuleCategory.COMBAT);
    }
    public static VelocityModule getInstance() {
        return TheresaModuleManager.getInstanceOrNull(VelocityModule.class);
    }
}
