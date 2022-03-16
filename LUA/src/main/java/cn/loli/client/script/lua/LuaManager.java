package cn.loli.client.script.lua;

import javax.script.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LuaManager {
    Map<String, CompiledScript> scripts = new HashMap<>();
    public static LuaManager INSTANCE = new LuaManager();
    public ScriptEngine engine;

    public static void main(String[] args) {
        INSTANCE.init();
    }

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

    public void init() {
        ScriptEngineManager sem = new ScriptEngineManager();
        engine = sem.getEngineByName("luaj");
//        ScriptEngineFactory f = e.getFactory();

        // add scripts
        for (File file : getFiles("C:\\Users\\Super\\Desktop\\luas")) {
            addScript(file.getName(), readFile(file));
        }

        //init lua
        for (Map.Entry<String, CompiledScript> entry : scripts.entrySet()) {
            try {
                entry.getValue().getEngine().eval("init()");
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
    }

    public void addScript(String name, String script) {
        try {
            if (engine == null) {
                System.out.println("engine is null");
            } else {
                CompiledScript cs = ((Compilable) engine).compile(script);
                scripts.put(name, cs);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public void removeScript(String name) {
        scripts.remove(name);
    }

    public void clear() {
        scripts.clear();
    }

    public void register(String name, String description, String category) {
        System.out.println("register name:" + name + " description:" + description + " category:" + category);

    }

}
