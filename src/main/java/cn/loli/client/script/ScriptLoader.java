package cn.loli.client.script;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.script.js.JSModule;
import cn.loli.client.script.js.JSTransformer;
import cn.loli.client.script.lua.LuaModule;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScriptLoader {
    public static Map<String, Globals> scripts = new HashMap<>();

    public static ScriptLoader INSTANCE = new ScriptLoader();
    ArrayList<Module> addons = new ArrayList<>();

    // get files form a folder
    public ArrayList<File> getFiles(String path) {
        ArrayList<File> list = new ArrayList<>();
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
        ArrayList<Module> remove = new ArrayList<>();
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
        Main.INSTANCE.println("[LuaManager] Lua Initializing...");
        // add scripts
        for (File file : getFiles(Main.INSTANCE.fileManager.scriptsDir.getAbsolutePath())) {
            try {
                if (file.getName().endsWith(".lua")) {
                    addScript(readFile(file));
                }

                if (file.getName().endsWith(".js")) {
                    //TODO: Read JavaScript
                    addJScript(readFile(file));
                }
            } catch (Exception e) {
                Main.INSTANCE.println(e.getMessage());
            }
        }

        //init lua
        for (Globals g : scripts.values()) {
            //TODO : ????????????lua????????????????????????
            String name, desc;

            try {
                name = g.get("get_name").call().toString();
                desc = g.get("get_desc").call().toString();
                Main.INSTANCE.moduleManager.addModule(new LuaModule(name, desc, g));
            } catch (Exception e) {
                Main.INSTANCE.println(e.getMessage());
            }
        }

        Main.INSTANCE.println("[LuaManager] Size: " + scripts.size());
    }

    public void addScript(String script) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(script);
        chunk.call();
        scripts.put(String.valueOf(globals.get("get_lua_name").call()), globals);
    }

    public void removeScript(String name) {
        scripts.remove(name);
    }

    public void clear() {
        scripts.clear();
    }

    public void register(String name, String description, String luaName, Map<String, Globals> modules) {
        try {
            Main.INSTANCE.moduleManager.addModuleNoReg(new LuaModule(name, description, modules.get(luaName)));
        } catch (Exception e) {
            Main.INSTANCE.println(e.getMessage());
        }
    }

    public void addJScript(String script ) {
        try {
            JSTransformer transformer = new JSTransformer(script);
            Main.INSTANCE.moduleManager.addModuleNoReg(new JSModule(transformer.getName(), transformer.getDesc(), transformer.getInvocable()));
            Main.INSTANCE.println("[LuaManager] Loaded .js script: " + transformer.getName());
        } catch (Exception e) {
            Main.INSTANCE.println(e.getMessage());
            Main.INSTANCE.println("[LuaManager] Failed to load JS script.");
        }

    }

}
