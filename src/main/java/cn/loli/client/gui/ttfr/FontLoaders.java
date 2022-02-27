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
            fonts.put("wqy_microhei" + i, getFont("wqy_microhei", i));
        }
        for (int i = 12; i <= 18; ++i) {
            fonts.put("targethub" + i, getFont("targethub", i));
        }
        for (int i = 12; i <= 18; ++i) {
            fonts.put("genshin" + i, getFont("genshin", i));
        }
        Main.INSTANCE.println("Fonts loading used " + (System.currentTimeMillis() - time) / 1000d + " seconds");
    }

    public HFontRenderer get(String name, int size) {
        if (fonts.get(name + size) == null) {
            return fonts.get("wqy_microhei14");
        }
        return fonts.get(name + size);
    }

    public HFontRenderer get(String name) {
        if (fonts.get(name) == null) {
            return fonts.get("wqy_microhei14");
        }
        return fonts.get(name);
    }

    private HFontRenderer getFont(String name, int size) {
        Font font;
        try {
            InputStream is = Utils.getFileFromResourceAsStream("theresa/font/" + name + ".ttf");
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.INSTANCE.println("Error loading font");
            font = new Font("default", 0, size);
        }

        return new HFontRenderer(font, size, true);
    }
}

