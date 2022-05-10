package dev.xix;

import cn.loli.client.module.ModuleManager;
import dev.xix.event.Event;
import dev.xix.event.bus.EventBus;

public final class TheresaClient {

    private final String clientName;
    private final String clientVersion;
    private final String[] clientDevelopers;

    private final EventBus<Event> eventBus;
    private final ModuleManager moduleManager;

    private TheresaClient() {
        this.clientName = "Theresa";
        this.clientVersion = "1.0-H";
        this.clientDevelopers = new String[]{"Mirror", "Yume", "xix"};
        this.eventBus = new EventBus<>();
        this.moduleManager = new ModuleManager();
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

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    private static final class TheresaClientInstanceInitializer {
        private static final TheresaClient INSTANCE = new TheresaClient();
    }

}
