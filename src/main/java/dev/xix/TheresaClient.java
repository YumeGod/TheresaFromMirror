package dev.xix;

import dev.xix.event.Event;
import dev.xix.event.bus.EventBus;
import dev.xix.feature.module.TheresaModuleManager;

public final class TheresaClient {

    private final String clientName;
    private final String clientVersion;
    private final String[] clientDevelopers;

    private final TheresaModuleManager moduleManager;
    private final EventBus<Event> eventBus;

    private TheresaClient() {
        this.clientName = "Theresa";
        this.clientVersion = "1.0-H";
        this.clientDevelopers = new String[]{"Mirror", "Yume", "xix"};

        this.eventBus = new EventBus<>();
        this.moduleManager = new TheresaModuleManager();
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

    public EventBus<Event> getEventBus() {return eventBus;}

    public TheresaModuleManager getModuleManager() {
        return moduleManager;
    }

    private static final class TheresaClientInstanceInitializer {
        private static final TheresaClient INSTANCE = new TheresaClient();
    }

}
