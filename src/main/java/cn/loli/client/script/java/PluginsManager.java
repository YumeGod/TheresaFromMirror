package cn.loli.client.script.java;

import cn.loli.client.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginsManager {
    public List<JavaPlugin> plugins = new ArrayList<>();
    public List<String> classes = new ArrayList<>();
    public URLClassLoader urlCL;

    public PluginsManager() {
        this.loadPlugins();
    }

    public void loadPlugins() {
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

        URL[] urls = new URL[files.length];

        for (int i = 0; i < files.length; i++) {
            try {
                urls[i] = new URL("file:" + files[i].getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        // 用Classloader逐个获取实例

        urlCL = new URLClassLoader(urls, this.getClass().getClassLoader());

        classes.forEach((s) -> {
            Class<?> clazz;
            try {
                if (s == null) {
                    return;
                }

                clazz = urlCL.loadClass(s);

                JavaPlugin instance = (JavaPlugin) clazz.newInstance();

                // 防止多次载入同一款插件
//                if (plugins.contains(instance)) {
//                    return;
//                }

                // 防止载入同一个插件的不同版本
//                for (JavaPlugin oldPlugin : plugins) {
//                    if (oldPlugin.pluginName.equals(instance.pluginName)) {
//                        float oldPluginVersion = oldPlugin.version;
//                        float newPluginVersion = instance.version;
//                        if (oldPluginVersion >= newPluginVersion) {
//                            return;
//                        } else {
//                            plugins.remove(oldPlugin);
//                            break;
//                        }
//                    }
//                }
                System.out.println("Loaded Plugin: " + instance.pluginName + " " + instance.version);
                plugins.add(instance);
            } catch (NoClassDefFoundError | Exception e) {
                e.printStackTrace();
            }
        });
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
                    if (Objects.equals(cn.superName, "cn/loli/client/script/java/JavaPlugin")) {
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


}