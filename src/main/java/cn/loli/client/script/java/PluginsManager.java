package cn.loli.client.script.java;

import cn.loli.client.Main;
import cn.loli.client.script.shadow.ShadowModuleManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginsManager {
    public List<JavaPlugin> plugins = new ArrayList<>();
    public Map<URLClassLoader, String> urlCL = new HashMap<>();

    public PluginsManager() {
        this.loadPlugins();
    }

    public void loadPlugins() {
        try {
            File scriptDir = Main.INSTANCE.fileManager.scriptsDir;

            // 检测及创建插件目录
            if (!scriptDir.exists()) {
                if (!scriptDir.mkdirs()) {
                    System.err.println("Create Plugin Folder Failed!");
                }
            }

            // 列出全部的jar包
            File[] files = scriptDir.listFiles((dir, name) -> name.endsWith(".jar"));

            if (files == null) {
                return;
            }

            for (File file : files) {
                urlCL.put(new URLClassLoader(new URL[]{new URL("file:" + file.getPath())}), this.getMain(file));
            }

            // 用Classloader逐个获取实例
            loading:
            for (int i = 0; i < urlCL.size(); i++) {
                URLClassLoader loader = (URLClassLoader) urlCL.keySet().toArray()[i];
                Class<?> clazz;
                try {
                    String pluginMain = urlCL.get(loader);

                    if (pluginMain == null) {
                        continue;
                    }

                    clazz = loader.loadClass(pluginMain);

                    System.out.println(clazz.getTypeName());
                    System.out.println(JavaPlugin.class.getTypeName());

                    JavaPlugin instance = (JavaPlugin) clazz.newInstance();

                    // 防止多次载入同一款插件
                    if (plugins.contains(instance)) {
                        continue;
                    }

                    // 防止载入同一个插件的不同版本
                    for (JavaPlugin oldPlugin : plugins) {
                        if (oldPlugin.pluginName.equals(instance.pluginName)) {
                            float oldPluginVersion = oldPlugin.version;
                            float newPluginVersion = instance.version;
                            if (oldPluginVersion >= newPluginVersion) {
                                continue loading;
                            } else {
                                plugins.remove(oldPlugin);
                                break;
                            }
                        }
                    }
                    System.out.println("Loaded Plugin: " + instance.pluginName + " " + instance.version);
                    plugins.add(instance);
                } catch (NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMain(File file) {
        try {
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().endsWith(".class")) {
                    InputStream input = zip.getInputStream(entry);
                    ClassReader cr = new ClassReader(input);
                    ClassNode cn = new ClassNode();
                    cr.accept(cn, ClassReader.SKIP_FRAMES);
                    if ("cn/loli/client/script/java/JavaPlugin".equals(cn.superName)) {
                        System.out.println("Found Plugin Main: " + cn.name);
                        return entry.getName().replaceAll("/", ".").replaceAll(".class", "");
                    }
                }
            }


            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void loadModules() {
        for (JavaPlugin plugin : plugins) {
            plugin.onModuleManagerLoad(new ShadowModuleManager(){});
        }
    }

}