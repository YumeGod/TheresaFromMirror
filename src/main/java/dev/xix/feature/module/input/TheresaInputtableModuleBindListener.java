package dev.xix.feature.module.input;

import cn.loli.client.events.KeyEvent;
import dev.xix.TheresaClient;
import dev.xix.event.bus.IEventListener;

public final class TheresaInputtableModuleBindListener {
    private final IEventListener<KeyEvent> eventListener = event -> {
        for (final IInputtableTheresaModule module : TheresaClient.getInstance().getInputManager().getInputtables()) {
            if (module.getKey() == event.getKey()) {
                module.pressKey();
            }
        }
    };
}
