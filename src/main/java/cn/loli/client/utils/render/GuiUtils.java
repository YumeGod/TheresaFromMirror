

package cn.loli.client.utils.render;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class GuiUtils {
    public static String formatTime(long l) {
        long minutes = l / 1000 / 60;

        l -= minutes * 1000 * 60;

        long seconds = l / 1000;

        l -= seconds * 1000;

        StringBuilder sb = new StringBuilder();

        if (minutes != 0) sb.append(minutes).append("min ");
        if (seconds != 0) sb.append(seconds).append("s ");

        if (l != 0 || minutes == 0 && seconds == 0) {
            sb.append(l).append("ms ");
        }

        return sb.substring(0, sb.length() - 1);
    }

    /*
     * By DarkStorm
     */
    public static Point calculateMouseLocation() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int scale = minecraft.gameSettings.guiScale;
        if (scale == 0)
            scale = 1000;
        int scaleFactor = 0;
        while (scaleFactor < scale && minecraft.displayWidth / (scaleFactor + 1) >= 320 && minecraft.displayHeight / (scaleFactor + 1) >= 240)
            scaleFactor++;
        return new Point(Mouse.getX() / scaleFactor, minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }
}
