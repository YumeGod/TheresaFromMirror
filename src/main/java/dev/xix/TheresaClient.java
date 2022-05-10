package dev.xix;

import cn.loli.client.module.ModuleManager;
import dev.xix.event.Event;
import dev.xix.event.bus.EventBus;
import dev.xix.feature.module.TheresaModuleManager;
import dev.xix.feature.module.input.TheresaInputManager;

public final class TheresaClient {

    private final String clientName;
    private final String clientVersion;
    private final String[] clientDevelopers;

    private final TheresaModuleManager moduleManager;
    private final TheresaInputManager inputManager;
    private final EventBus<Event> eventBus;

    private TheresaClient() {
        this.clientName = "Theresa";
        this.clientVersion = "1.0-H";
        this.clientDevelopers = new String[]{"Mirror", "Yume", "xix"};

        this.moduleManager = new TheresaModuleManager();
        this.eventBus = new EventBus<>();
        inputManager = new TheresaInputManager();
    }

    public static TheresaClient getInstance() {
        return TheresaClientInstanceInitializer.INSTANCE;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public String[] getClientDevelopers() {
        return clientDevelopers;
    }

    public TheresaModuleManager getModuleManager() {
        return moduleManager;
    }

    public EventBus<Event> getEventBus() {
        return eventBus;
    }

    public TheresaInputManager getInputManager() {
        return inputManager;
    }

    private static final class TheresaClientInstanceInitializer {
        private static final TheresaClient INSTANCE = new TheresaClient();
    }

}
