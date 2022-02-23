

package cn.loli.client.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorUtils {
    /**
     * @param colorStr e.g. "#FFFFFF"
     * @return Color object
     */
    public static Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    public static Color intToColor(int color) {
        Color c1 = new Color(color);
        return new Color(c1.getRed(),c1.getGreen(),c1.getBlue(),color >> 24 & 255);
    }

    /**
     * Converts the hue, saturation and value color model to the red, green and
     * blue color model
     *
     * @param hue        - A positive integer, which, modulo 360, will represent the
     *                   hue of the color
     * @param saturation - An integer between 0 and 100
     * @param value      - An integer between 0 and 100
     * @return rgb color integer
     */
    public static int hsvToRgb(int hue, int saturation, int value) {
        // Source: en.wikipedia.org/wiki/HSL_and_HSV#Converting_to_RGB#From_HSV
        hue %= 360;
        float s = (float) saturation / 100;
        float v = (float) value / 100;
        float c = v * s;
        float h = (float) hue / 60;
        float x = c * (1 - Math.abs(h % 2 - 1));
        float r, g, b;
        switch (hue / 60) {
            case 0:
                r = c;
                g = x;
                b = 0;
                break;
            case 1:
                r = x;
                g = c;
                b = 0;
                break;
            case 2:
                r = 0;
                g = c;
                b = x;
                break;
            case 3:
                r = 0;
                g = x;
                b = c;
                break;
            case 4:
                r = x;
                g = 0;
                b = c;
                break;
            case 5:
                r = c;
                g = 0;
                b = x;
                break;
            default:
                return 0;
        }
        float m = v - c;
        return ((int) ((r + m) * 255) << 16) | ((int) ((g + m) * 255) << 8) | ((int) ((b + m) * 255));
    }

    /**
     * Converts the red, green and blue color model to the hue, saturation and
     * value color model
     *
     * @param rgb color integer
     * @return A 3-length array containing hue, saturation and value, in that
     * order. Hue is an integer between 0 and 359, or -1 if it is
     * undefined. Saturation and value are both integers between 0 and
     * 100
     */
    public static int[] rgbToHsv(int rgb) {
        float r = (float) ((rgb & 0xff0000) >> 16) / 255;
        float g = (float) ((rgb & 0x00ff00) >> 8) / 255;
        float b = (float) (rgb & 0x0000ff) / 255;
        float M = r > g ? (Math.max(r, b)) : (Math.max(g, b));
        float m = r < g ? (Math.min(r, b)) : (Math.min(g, b));
        float c = M - m;
        float h;
        if (M == r) {
            h = ((g - b) / c);
            while (h < 0)
                h += 6;
            h %= 6;
        } else if (M == g) {
            h = ((b - r) / c) + 2;
        } else {
            h = ((r - g) / c) + 4;
        }
        h *= 60;
        float s = c / M;
        return new int[]{c == 0 ? -1 : (int) h, (int) (s * 100), (int) (M * 100)};
    }

    /**
     * Draws a rectangle with possibly different colors in different corners
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param coltl  - the color of the top left corner
     * @param coltr  - the color of the top right corner
     * @param colbl  - the color of the bottom left corner
     * @param colbr  - the color of the bottom right corner
     */
    public static void drawGradientRect(int left, int top, int right, int bottom, int coltl, int coltr, int colbl, int colbr) {
        drawGradientRect(left, top, right, bottom, coltl, coltr, colbl, colbr, 0);
    }

    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float) c.getRed();
        float g = 0.003921569f * (float) c.getGreen();
        float b = 0.003921569f * (float) c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    /**
     * Draws a rectangle with possibly different colors in different corners
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param coltl  - the color of the top left corner
     * @param coltr  - the color of the top right corner
     * @param colbl  - the color of the bottom left corner
     * @param colbr  - the color of the bottom right corner
     * @param zLevel
     */
    public static void drawGradientRect(int left, int top, int right, int bottom, int coltl, int coltr, int colbl, int colbr, int zLevel) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        renderer.pos(right, top, zLevel).color((coltr & 0x00ff0000) >> 16, (coltr & 0x0000ff00) >> 8,
                (coltr & 0x000000ff), (coltr & 0xff000000) >>> 24).endVertex();
        renderer.pos(left, top, zLevel).color((coltl & 0x00ff0000) >> 16, (coltl & 0x0000ff00) >> 8, (coltl & 0x000000ff),
                (coltl & 0xff000000) >>> 24).endVertex();
        renderer.pos(left, bottom, zLevel).color((colbl & 0x00ff0000) >> 16, (colbl & 0x0000ff00) >> 8,
                (colbl & 0x000000ff), (colbl & 0xff000000) >>> 24).endVertex();
        renderer.pos(right, bottom, zLevel).color((colbr & 0x00ff0000) >> 16, (colbr & 0x0000ff00) >> 8,
                (colbr & 0x000000ff), (colbr & 0xff000000) >>> 24).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
