

package cn.loli.client.value;

import cn.loli.client.gui.IColorPickerCallback;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.awt.*;

public class ColorValue extends Value<Color> {
    public IColorPickerCallback colorPickerCallback;

    public ColorValue(String name, Color defaultVal) {
        super(name, defaultVal, null);

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
}
