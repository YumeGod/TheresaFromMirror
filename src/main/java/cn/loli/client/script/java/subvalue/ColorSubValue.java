

package cn.loli.client.script.java.subvalue;

import cn.loli.client.gui.IColorPickerCallback;
import cn.loli.client.utils.render.ColorUtils;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.ColorValue;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ColorSubValue extends ColorValue {
    public IColorPickerCallback colorPickerCallback;
    private float hue = -1;
    private float saturation;
    private float brightness;
    private float alpha;

    public ColorSubValue(String name, Color defaultVal) {
        super(name, defaultVal);

        colorPickerCallback = new IColorPickerCallback() {
            @Override
            public Color getColor() {
                return getObject();
            }

            @Override
            public void setColor(Color color) {
                setObject(color);
            }
        };
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName() + "_rgba", getObject().getRed() + "," + getObject().getGreen() + "," + getObject().getBlue() + "," + getObject().getAlpha());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(getName() + "_rgba")) {
            JsonElement element = obj.get(getName() + "_rgba");

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                String[] rgba = obj.get(getName() + "_rgba").getAsString().split(",");

                if (rgba.length == 4) {
                    int r = Integer.parseInt(rgba[0]);
                    int g = Integer.parseInt(rgba[1]);
                    int b = Integer.parseInt(rgba[2]);
                    int a = Integer.parseInt(rgba[3]);
                    setObject(new Color(r, g, b, a));
                }
            }
        }
    }


    public void draw(float x, float y, float width, float height, float mouseX, float mouseY) {
        if (hue == -1) {
            float[] vals = Color.RGBtoHSB(getObject().getRGB() >> 16 & 255, getObject().getRGB() >> 8 & 255, getObject().getRGB() & 255, null);
            hue = vals[0];
            saturation = vals[1];
            brightness = vals[2];
            alpha = (getObject().getRGB() >> 24 & 255) / 255f;
        }
        // Saturation
        RenderUtils.drawGradientSideways(x, y, x + width, y + height, 0x00ffffff, Color.HSBtoRGB(1 - hue, 1, 1));

        // Brightness
        RenderUtils.drawGradientRect(x, y - 5, x + width, y + height, 0xff000000, 0x00ffffff);

        // hue
        float barHeight = 40;
        float hueSeg = barHeight / 5.0f;
        float hueBarTop = y + height - 40;
        int i = 0;
        while (i < 5) {
            RenderUtils.drawGradientRect(x + 42, hueBarTop, x + 48, hueBarTop + hueSeg, Color.HSBtoRGB(1.0f - 0.2f * (i + 1), 1, 1), Color.HSBtoRGB(1.0f - 0.2f * (i), 1, 1));
            if (i < 4) {
                hueBarTop += hueSeg;
            }
            ++i;
        }

        //RenderUtils.drawRect(x + 42, y + height - 40, x + 48, y + height, 0x88ff0000);

        // Alpha
        RenderUtils.drawRect(x + 50, y, x + 56, y + height, new Color(82, 82, 82, 255).getRGB());
        RenderUtils.drawGradientRect(x + 50, y, x + 56, y + height, ColorUtils.reAlpha(getObject().getRGB(), 1), ColorUtils.reAlpha(getObject().getRGB(), 0));

        double bY = height - brightness * height;
        double sX = saturation * width;
        RenderUtils.drawFilledCircle(x + sX, y + bY, 2.4, new Color(0, 0, 0, 255).getRGB(), 5);
        RenderUtils.drawFilledCircle(x + sX, y + bY, 2, -1, 5);


        double hueY = hue * height;
        RenderUtils.drawRect(x + 42, (float) (y + hueY), x + 48, (float) (y + hueY + 1), -1);

        double alphaY = alpha * height;
        RenderUtils.drawRect(x + 50, (float) (y + alphaY), x + 56, (float) (y + alphaY + 1), -1);

        if (Mouse.isButtonDown(0)) {
            if (isHovered(x + 42, y, x + 48, y + height, ((int) mouseX), ((int) mouseY))) {
                hue = (mouseY - y) / height;
            }
            if (isHovered(x, y, x + width, y + height, ((int) mouseX), ((int) mouseY))) {
                brightness = 1 - (mouseY - y) / height;
            }
            if (isHovered(x, y, x + width, y + height, ((int) mouseX), ((int) mouseY))) {
                saturation = (mouseX - x) / width;
            }
            if (isHovered(x + 50, y, x + 56, y + height, ((int) mouseX), ((int) mouseY))) {
                alpha = (mouseY - y) / height;
            }
        }
        this.setObject(ColorUtils.intToColor(ColorUtils.reAlpha(Color.HSBtoRGB(1 - hue, saturation, brightness), alpha)));
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

}
