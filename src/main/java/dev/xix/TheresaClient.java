package dev.xix;

import cn.loli.client.module.ModuleManager;

public final class TheresaClient {

    private final String clientName;
    private final String clientVersion;
    private final String[] clientDevelopers;

    private final ModuleManager moduleManager;

    private TheresaClient() {
        this.clientName = "Theresa";
        this.clientVersion = "1.0-H";
        this.clientDevelopers = new String[]{"Mirror", "Yume", "xix"};

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

    private static final class TheresaClientInstanceInitializer {
        private static final TheresaClient INSTANCE = new TheresaClient();
    }

}
