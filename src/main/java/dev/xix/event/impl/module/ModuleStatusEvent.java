package dev.xix.event.impl.module;

import dev.xix.event.EventCancellable;
import dev.xix.feature.module.AbstractTheresaModule;

public final class ModuleStatusEvent extends EventCancellable {
    private final AbstractTheresaModule module;
    private boolean toggled;

    public ModuleStatusEvent(final AbstractTheresaModule module) {
        this.module = module;
        this.toggled = module.getEnabled();
    }

    public boolean isToggling() {
        return toggled;
    }

    public AbstractTheresaModule getBackingModule() {
        return module;
    }
}
