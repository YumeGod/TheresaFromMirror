package cn.loli.client.script.java;


public class JavaPlugin {
    public String pluginName;
    public String author;
    public float version;

    public JavaPlugin(String pluginName, String author, float version){
        this.pluginName = pluginName;
        this.author = author;
        this.version = version;
    }

    public void onModuleManagerLoad(ModuleManager modManager){
        System.out.println(1);
    }

    public void onCommandManagerLoad(ModuleManager commandManager){
        System.out.println(1);
    }
}