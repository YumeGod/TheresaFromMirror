package dev.xix;

import dev.xix.event.Event;
import dev.xix.event.bus.EventBus;
import dev.xix.feature.module.TheresaModuleManager;
import dev.xix.feature.module.input.TheresaInputManager;
import dev.xix.gui.element.ElementManager;

public final class TheresaClient {

    private final String clientName;
    private final String clientVersion;
    private final String[] clientDevelopers;

    private final EventBus<Event> eventBus;
    private final TheresaModuleManager moduleManager;
    private final TheresaInputManager inputManager;
    private final ElementManager elementManager;


    private TheresaClient() {
        this.clientName = "Theresa";
        this.clientVersion = "1.0-H";
        this.clientDevelopers = new String[]{"Mirror", "Yume", "xix"};

        this.eventBus = new EventBus<>();
        this.moduleManager = new TheresaModuleManager();
        this.elementManager = new ElementManager();
        this.inputManager = new TheresaInputManager();
        eventBus.register(elementManager);
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

    public ElementManager getElementManager() {return elementManager;}

    private static final class TheresaClientInstanceInitializer {
        private static final TheresaClient INSTANCE = new TheresaClient();
    }

}
