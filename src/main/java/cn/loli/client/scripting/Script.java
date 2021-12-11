

package cn.loli.client.scripting;

import cn.loli.client.Main;

import java.util.ArrayList;
import java.util.List;

public class Script {
    private final String name;
    private final String version;
    private final List<ScriptModule> modules = new ArrayList<>();

    public Script(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<ScriptModule> getModules() {
        return modules;
    }

    public void register() {
        for (ScriptModule module : modules) {
            Main.INSTANCE.moduleManager.addScriptModule(module);
        }
    }
}
