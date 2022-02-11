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
        for (int i = 12; i < 16; ++i) {
            fonts.put("inter" + i, getFont("inter", i));
        }
        for (int i = 12; i < 16; ++i) {
            fonts.put("nunito" + i, getFont("nunito", i));
        }
        for (int i = 12; i < 16; ++i) {
            fonts.put("opensans" + i, getFont("opensans", i));
        }
        for (int i = 12; i < 16; ++i) {
            fonts.put("roboto" + i, getFont("roboto", i));
        }
        for (int i = 12; i < 16; ++i) {
            fonts.put("ubuntu" + i, getFont("ubuntu", i));
        }
        for (int i = 12; i < 16; ++i) {
            fonts.put("wqy_microhei" + i, getFont("wqy_microhei", i));
        }
        System.out.println("Fonts loading used " + (System.currentTimeMillis() - time) / 1000d + " seconds");
    }

    private HFontRenderer getFont(String name, int size) {
        Font font;
        try {
            InputStream is = Utils.getFileFromResourceAsStream("theresa/font/" + name + ".ttf");
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }

        return new HFontRenderer(font, size, true);
    }
}

