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
        for (int i = 12; i <= 16; ++i) {
            fonts.put("inter" + i, getFont("inter", i));
        }
        for (int i = 12; i <= 16; ++i) {
            fonts.put("nunito" + i, getFont("nunito", i));
        }
        for (int i = 12; i <= 16; ++i) {
            fonts.put("opensans" + i, getFont("opensans", i));
        }
        for (int i = 12; i <= 24; ++i) {
            fonts.put("roboto" + i, getFont("roboto", i));
        }
        for (int i = 12; i <= 16; ++i) {
            fonts.put("ubuntu" + i, getFont("ubuntu", i));
        }
        for (int i = 12; i <= 18; ++i) {
            fonts.put("tiejili" + i, getFont("tiejili", i));
        }
        for (int i = 12; i <= 18; ++i) {
            fonts.put("targethub" + i, getFont("targethub", i));
        }
        for (int i = 12; i <= 20; ++i) {
            fonts.put("genshin" + i, getFont("genshin", i));
        }
        for (int i = 12; i <= 18; ++i) {
            fonts.put("dos" + i, getFont("dos", i));
        }
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
}

