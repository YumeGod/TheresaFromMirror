package cn.loli.client.script.java;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @description: Java插件管理
 * @author: QianXia
 * @create: 2020/10/5 15:54
 **/
public class PluginsManager {
    public List<JavaPlugin> plugins = new ArrayList<>();
    public Map<URLClassLoader, String> urlCL = new HashMap<>();

    public PluginsManager(){
        this.loadPlugins(false);
    }

    public static void main(String[] args) {
        PluginsManager manager = new PluginsManager();
        manager.loadPlugins(true);
    }

    public void loadPlugins(boolean reload){
        try {
            File luneDir = new File("C:\\Users\\Super\\Desktop\\luas");
            File pluginDir = new File(luneDir, "Plugins");

            // 检测及创建插件目录
            if(!pluginDir.exists()){
                if(!pluginDir.mkdirs()){
                    System.err.println("Create Plugin Folder Failed!");
                }
            }

            // 列出全部的jar包
            File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));

            if(files == null){
                return;
            }

            for (File file : files) {
                urlCL.put(new URLClassLoader(new URL[]{new URL("file:" + file.getPath())}), this.getMain(file));
            }

            // 用Classloader逐个获取实例
            loading: for (int i = 0; i < urlCL.size(); i++) {
                URLClassLoader loader = (URLClassLoader) urlCL.keySet().toArray()[i];
                Class<?> clazz;
                try {
                    String pluginMain = urlCL.get(loader);

                    if(pluginMain == null) {
                        continue;
                    }
                    clazz = loader.loadClass(pluginMain);
                    JavaPlugin instance = (JavaPlugin) clazz.newInstance();

                    // 防止多次载入同一款插件
                    if(plugins.contains(instance)){
                        continue;
                    }

                    // 防止载入同一个插件的不同版本
                    for (JavaPlugin oldPlugin : plugins) {
                        if (oldPlugin.pluginName.equals(instance.pluginName)) {
                            float oldPluginVersion = oldPlugin.version;
                            float newPluginVersion = instance.version;
                            if(oldPluginVersion >= newPluginVersion){
                                continue loading;
                            }else{
                                plugins.remove(oldPlugin);
                                break;
                            }
                        }
                    }
                    System.out.println("Loaded Plugin: " + instance.pluginName + " " + instance.version);
                    plugins.add(instance);
                }catch (NoClassDefFoundError e){
                    e.printStackTrace();
                }
            }

            // 重载需要重新调用这些函数
//            if(reload){
//                this.onModuleManagerLoad(Lune.moduleManager, true);
//                this.onCommandManagerLoad(Lune.commandManager);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMain(File file) {
        String main = null;
        try {
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if(entry.getName().endsWith(".class")) {
                    InputStream input = zip.getInputStream(entry);
                    ClassReader cr = new ClassReader(input);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, ClassReader.SKIP_FRAMES);
                    if("cn/loli/client/script/java/JavaPlugin".equals(cn.superName)) {
                        return entry.getName().replaceAll("/", ".").replaceAll(".class", "");
                    }
                }
            }


            zip.close();
        } catch (ZipException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return main;
    }

    public void loadModules(){

    }
//
//    public boolean isPluginEnabled(JavaPlugin plugin){
//        for (Object value : ModuleManager.pluginModsList.values()) {
//            if (value instanceof JavaPlugin) {
//                if (value.equals(plugin)) {
//                    return true;
//                }
//            }
//        }
//
//        for (Object value : CommandManager.pluginCommands.values()) {
//            if (value instanceof JavaPlugin) {
//                if (value.equals(plugin)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    public void setPluginState(LunePlugin plugin, boolean state) {
//        AtomicReference<Mod> tempMod = new AtomicReference<>();
//        AtomicReference<Command> tempCmd = new AtomicReference<>();
//
//        if (state) {
//            ModuleManager.disabledPluginList.forEach((mod, value) -> {
//                if (value instanceof LunePlugin) {
//                    if (value.equals(plugin)) {
//                        tempMod.set(mod);
//                    }
//                }
//            });
//
//            ModuleManager.disabledPluginList.remove(tempMod.get());
//            ModuleManager.pluginModsList.put(tempMod.get(), plugin);
//            ModuleManager.modList.add(tempMod.get());
//
//            CommandManager.disabledPluginCommands.forEach((cmd, value) -> {
//                if (value instanceof LunePlugin) {
//                    if (value.equals(plugin)) {
//                        tempCmd.set(cmd);
//                    }
//                }
//            });
//
//            CommandManager.disabledPluginCommands.remove(tempCmd.get());
//            CommandManager.pluginCommands.put(tempCmd.get(), plugin);
//            CommandManager.commands.add(tempCmd.get());
//        } else {
//            ModuleManager.pluginModsList.forEach((mod, value) -> {
//                if (value instanceof LunePlugin) {
//                    if (value.equals(plugin)) {
//                        tempMod.set(mod);
//                    }
//                }
//            });
//
//            ModuleManager.pluginModsList.remove(tempMod.get());
//            ModuleManager.modList.remove(tempMod.get());
//            ModuleManager.disabledPluginList.put(tempMod.get(), plugin);
//
//
//            CommandManager.pluginCommands.forEach((cmd, value) -> {
//                if (value instanceof LunePlugin) {
//                    if (value.equals(plugin)) {
//                        tempCmd.set(cmd);
//                    }
//                }
//            });
//
//            CommandManager.pluginCommands.remove(tempCmd.get());
//            CommandManager.commands.remove(tempCmd.get());
//            CommandManager.disabledPluginCommands.put(tempCmd.get(), plugin);
//        }
//        Lune.moduleManager.sortModules();
//    }
//
//    public void onModuleManagerLoad(ModuleManager modManager, boolean reload){
//        for (LunePlugin plugin : plugins) {
//            plugin.onModuleManagerLoad(modManager);
//        }
//        if(reload) {
//            Lune.moduleManager.sortModules();
//        }
//    }
//
//    public void onCommandManagerLoad(CommandManager commandManager){
//        for (LunePlugin plugin : plugins) {
//            plugin.onCommandManagerLoad(commandManager);
//        }
//    }
//
//    public void onClientStart(Lune lune){
//        for (LunePlugin plugin : plugins) {
//            plugin.onClientStart(lune);
//        }
//    }
//
//    public void onClientStop(Lune lune){
//        for (LunePlugin plugin : plugins) {
//            plugin.onClientStop(lune);
//        }
//    }

    public static class Check extends Thread{
        private Thread thread;

        public Check(Thread thread) {
            this.thread = thread;
        }

        @Override
        public void run() {
            while (thread.isAlive()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            Main.needReload = true;
            this.interrupt();
        }
    }
}