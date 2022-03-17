package cn.loli.client.script.java;

import cn.loli.client.Main;
import cn.loli.client.module.ModuleManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginsManager {

    public ModuleManager moduleManager = null; //TODO: Implement
    public Map<Class<? extends SubModule>, SubModule> modules = new HashMap<>();
    public URLClassLoader urlCL;

    public PluginsManager() {
        this.loadPlugins();
    }

    public void loadPlugins() {
        File scriptDir = Main.INSTANCE.fileManager.scriptsDir;

        // 检测及创建插件目录
        if (!scriptDir.exists()) {
            if (!scriptDir.mkdirs()) {
                System.err.println("[LuaManager] Create Plugin Folder Failed!");
            }
        }

        // 列出全部的jar包
        File[] files = scriptDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (files == null) {
            return;
        }

        URL[] urls = new URL[files.length];

        for (int i = 0; i < files.length; i++) {
            try {
                urls[i] = files[i].toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        urlCL = new URLClassLoader(urls, this.getClass().getClassLoader());

        // 用Classloader逐个获取实例
        for (File f : files) {
            List<String> s = getActiveClass(f, true);
            Class<?> clazz;
            try {
                if (s == null) return;
                for (String name : s) {
                    clazz = urlCL.loadClass(name);
                    SubModule instance = (SubModule) clazz.newInstance();

                    modules.compute(instance.getClass(), (javaPluginClass, javaPlugin) -> {
                        if (javaPlugin == null) {
                            return instance;
                        } else {
                            return javaPlugin;
                        }
                    });

                    Main.INSTANCE.moduleManager.addModule(instance);
                    Main.INSTANCE.println("[LuaManager]Loaded Plugin: " + instance.getName() + " " + instance.getCategory());
                }
            } catch (NoClassDefFoundError | Exception e) {
                e.printStackTrace();
            }
        }

        // 用Classloader逐个获取实例
        for (File f : files) {
            List<String> s = getActiveClass(f, false);
            Class<?> clazz;
            try {
                if (s == null) return;
                for (String name : s) {
                    clazz = urlCL.loadClass(name);
                    ActiveUtils newInstance =
                            (ActiveUtils) clazz.newInstance();
                }
                Main.INSTANCE.println("[LuaManager] Get ur utils active: " + s);
            } catch (NoClassDefFoundError | Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getActiveClass(File file, boolean isModules) {
        List<String> activeClass = new ArrayList<>();
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
                    String i = isModules ? "cn/loli/client/script/java/SubModule" : "cn/loli/client/script/java/ActiveUtils";
                    if (Objects.equals(cn.superName, i)) {
                        Main.INSTANCE.println("Found Plugin Main: " + cn.name);
                        activeClass.add(entry.getName().replaceAll("/", ".").replaceAll(".class", ""));
                    }
                }
            }

            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return activeClass;
    }


}