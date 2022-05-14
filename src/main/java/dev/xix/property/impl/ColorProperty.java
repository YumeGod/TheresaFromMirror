package dev.xix.property.impl;

import cn.loli.client.utils.render.ColorUtils;
import cn.loli.client.utils.render.RenderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.xix.property.AbstractTheresaProperty;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.function.Supplier;

public class ColorProperty extends AbstractTheresaProperty<Color> {
    public ColorProperty(final String name, final Color value, final Supplier<Boolean> dependency) {
        super(name, value, dependency);
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName() + "_rgba", getPropertyValue().getRed() + "," + getPropertyValue().getGreen() + "," + getPropertyValue().getBlue() + "," + getPropertyValue().getAlpha());
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
                    setPropertyValue(new Color(r, g, b, a));
                }
            }
        }
    }
    public ColorProperty(final String name, final Color value) {
        super(name, value, () -> true);
    }

    public void draw(float x, float y, float width, float height, float mouseX, float mouseY) {
        // todo: NOT AVAILABLE
    }

}
