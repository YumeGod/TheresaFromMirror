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

public class FontLoaders {

    public Map<String, HFontRenderer> fonts = new HashMap<>();

    public FontLoaders() {
        System.setProperty("java.awt.headless", "true");

        long time = System.currentTimeMillis();

        load("inter", 12, 16);
        load("nunito", 12, 16);
        load("opensans", 12, 16);
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
            return fonts.get("tiejili");
        }
        return fonts.get(name + size);
    }

    public HFontRenderer get(String name) {
        if (fonts.get(name) == null) {
            return fonts.get("tiejili");
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

