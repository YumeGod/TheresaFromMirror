/*
 * Decompiled with CFR 0_132.
 */
package cn.loli.client.script.java.sfontmanager;

import cn.loli.client.Main;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SFontLoader {

    public Map<String, FontRenderer> fonts = new HashMap<>();

    public SFontLoader() {

        long time = System.currentTimeMillis();

        for (File file : getFiles(Main.INSTANCE.fileManager.fontDir.getAbsolutePath())) {
            if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf")){
                for (int i = 12; i <= 32; ++i)
                    fonts.put(file.getName().replace(".ttf", "").replace(".otf", "") + i, getFont(file, i));
            }

        }
        Main.INSTANCE.println("Shadow Font Loader loading used " + (System.currentTimeMillis() - time) / 1000d + " seconds");
    }

    public FontRenderer get(String name, int size) {
        if (fonts.get(name + size) == null) {
            Main.INSTANCE.println(name + size + " not found");
            return fonts.get("default16");
        }
        return fonts.get(name + size);
    }

    public FontRenderer get(String name) {
        if (fonts.get(name) == null) {
            Main.INSTANCE.println(name + " not found");
            return fonts.get("default16");
        }
        return fonts.get(name);
    }

    private FontRenderer getFont(File file, int size) {
        Font font;
        try {
            font = Font.createFont(0, file);
            font = font.deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.INSTANCE.println("Error loading font");
            font = new Font("default", Font.PLAIN, size);
        }

        return new FontRenderer(font, size, true);
    }

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
}

