package dev.xix.util.render;

import net.minecraft.client.Minecraft;

public final class RenderingUtil {
    public static float getScaledFactor() {
        float scaledWidth = Minecraft.getMinecraft().displayWidth;
        float scaledHeight = Minecraft.getMinecraft().displayHeight;
        float scaleFactor = 1;
        boolean flag = Minecraft.getMinecraft().isUnicode();
        float i = Minecraft.getMinecraft().gameSettings.guiScale;

        if (i == 0)
        {
            i = 1000;
        }

        while (scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240)
        {
            ++scaleFactor;
        }

        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1)
        {
            --scaleFactor;
        }

        return scaleFactor;
    }
}
