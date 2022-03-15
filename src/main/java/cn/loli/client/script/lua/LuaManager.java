package cn.loli.client.script.lua;

import cn.loli.client.Main;
import cn.loli.client.module.LuaModule;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LuaManager {
    public static Map<String, Globals> scripts = new HashMap<>();

    public static LuaManager INSTANCE = new LuaManager();

    // get files form a folder
    public ArrayList<File> getFiles(String path) {
        ArrayList<File> list = new ArrayList();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(getFiles(f.getAbsolutePath()));
            }
            if (f.isFile()) {
                list.add(f);
            }
        }
        return list;
    }

    // read content from file
    public String readFile(File file) {
        StringBuilder content = new StringBuilder();
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // reload the lua manager
    public void reload() {
        ArrayList<Module> remove = new ArrayList();
        for (Module module : Main.INSTANCE.moduleManager.getModules()) {
            if (module.getCategory() == ModuleCategory.LUA) {
                remove.add(module);
            }
        }
        Main.INSTANCE.moduleManager.getModules().removeAll(remove);
        scripts.clear();
        init();
    }

    public void init() {
        System.out.println("[LuaManager] Initializing...");
        // add scripts
        for (File file : getFiles(Main.INSTANCE.fileManager.scriptsDir.getAbsolutePath())) {
            addScript(readFile(file));
            System.out.println("[LuaManager] Loaded script: " + file.getName());
        }

        //init lua
        for (Globals g: scripts.values()) {
            g.get("init").call();
        }
        System.out.println("[LuaManager] Initialized.");
        System.out.println("[LuaManager] Size: " + scripts.size());
    }

    public void addScript(String script) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(script);
        chunk.call();
        scripts.put(String.valueOf(globals.get("getLuaName").call()), globals);
        System.out.println("Put script: " + globals.get("getLuaName").call());
        System.out.println("Size: " + scripts.size());
    }

    public void removeScript(String name) {
        scripts.remove(name);
    }

    public void clear() {
        scripts.clear();
    }

    public void register(String name, String description, String luaName,Map<String, Globals> modules) {
        System.out.println("[LuaManager] Registering lua. " + modules.size());
        Main.INSTANCE.moduleManager.addModule(new LuaModule(name, description, modules.get(luaName)));
    }
}
