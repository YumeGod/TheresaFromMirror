package cn.loli.client.script.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LuaManager {
    Map<String, Globals> scripts = new HashMap<>();
    public static LuaManager INSTANCE = new LuaManager();

    public static void main(String[] args) {
        INSTANCE.init();

        while (true) {
            Event.onUpdate();
        }
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
        // add scripts
        for (File file : getFiles("C:\\Users\\Super\\Desktop\\luas")) {
            addScript(file.getName(), readFile(file));
        }

        //init lua
        for (Map.Entry<String, Globals> entry : scripts.entrySet()) {
            Globals globals = entry.getValue();
            globals.get("init").call();
//            System.out.println(globals.get("getLuaName").call());
            System.out.println("loaded name:" + globals.get("getLuaName").call() + " author:" + globals.get("getAuthor").call() + " description:" + globals.get("getDescription").call());
        }
    }

    public void addScript(String name, String script) {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.load(script);
        chunk.call();
        scripts.put(name, globals);
    }

    public void removeScript(String name) {
        scripts.remove(name);
    }

    public void clear() {
        scripts.clear();
    }

    public void register(String name,String description,String category){
        System.out.println("register name:"+name+" description:"+description+" category:"+category);

    }

}
