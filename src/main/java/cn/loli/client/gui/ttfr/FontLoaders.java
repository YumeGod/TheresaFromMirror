/*
 * Decompiled with CFR 0_132.
 */
package cn.loli.client.gui.ttfr;

import cn.loli.client.Main;
import cn.loli.client.utils.Utils;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FontLoaders {

    public Map<String, HFontRenderer> fonts = new HashMap<>();

    public FontLoaders() {
        System.setProperty("java.awt.headless", "true");

        long time = System.currentTimeMillis();

        load("inter", 14, 18);
        load("nunito", 12, 18);
        load("opensans", 12, 18);
        load("roboto", 12, 24);
        load("heiti", 12, 24);
        load("genshin", 12, 24);
        load("ubuntu", 12, 16);
        load("tiejili", 12, 18);
        load("targethub", 12, 18);
        load("dos", 12, 18);
        Main.INSTANCE.println("Fonts loading used " + (System.currentTimeMillis() - time) / 1000d + " seconds");
    }

    public HFontRenderer get(String name, int size) {
        if (fonts.get(name + size) == null) {
            fonts.put(name + size, getFont(name, size));
            Main.INSTANCE.println("Font " + name + " not found, loading default font");
        }

        return fonts.get(name + size);
    }

    public HFontRenderer get(String name) {
        if (fonts.get(name) == null) {
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(name);
            int size = Integer.parseInt(m.replaceAll(""));
            fonts.put(name, getFont(name.replaceAll(m.replaceAll(""), ""), size));
        }

        return fonts.get(name);
    }

    private HFontRenderer getFont(String name, int size) {
        Font font;
        try {
            InputStream is = Utils.getFileFromResourceAsStream("theresa/font/" + name + ".ttf");
            font = Font.createFont(0, Objects.requireNonNull(is)).deriveFont(Font.PLAIN, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.INSTANCE.println("Error loading font");
            font = new Font("Arial", Font.PLAIN, size);
        }

        return new HFontRenderer(font, size, true);
    }

    private void load(String name, int minSize, int maxSize) {
        for (int i = minSize; i <= maxSize; ++i)
            fonts.put(name + i, getFont(name, i));
    }
}

