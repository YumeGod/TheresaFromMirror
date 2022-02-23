

package cn.loli.client.notifications;

import cn.loli.client.Main;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Locale;

public class Notification {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NotificationType type;
    private final String title;
    private final String messsage;
    private long start;

    private float localHeightOffset;

    private final long fadedIn;
    private final long fadeOut;
    private final long end;


    public Notification(NotificationType type, String title, String messsage, int length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = 200L * length;
        fadeOut = fadedIn + 500L * length;
        end = fadeOut + fadedIn;
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public long getStart() {
        return start;
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render(short number) {
        ScaledResolution res = new ScaledResolution(mc);
        double offset;
        int width = Math.max(Minecraft.getMinecraft().fontRendererObj.getStringWidth(messsage) + 20, Minecraft.getMinecraft().fontRendererObj.getStringWidth(title) * 2 + 20);
        int height = 25;
        long time = getTime();

        if (time < fadedIn) {
            offset = Math.tanh(time / (double) (fadedIn) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * 3.0) * width);
        } else {
            offset = width;
        }

        Color backgroundColor = new Color(255, 255, 255, 255);
        Color ribbonColor;

        if (type == NotificationType.INFO)
            ribbonColor = new Color(68, 119, 255);
        else if (type == NotificationType.WARNING)
            ribbonColor = new Color(250, 210, 0);
        else {
            ribbonColor = new Color(255, 104, 104);
//            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
        }

        float heightOffset = 20 + number * (height + 5);
        localHeightOffset = AnimationUtils.smoothAnimation(localHeightOffset, heightOffset, 30, .3f);
        heightOffset = localHeightOffset;

        RenderUtils.drawRoundRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height - heightOffset, res.getScaledWidth() + 4, res.getScaledHeight() - 5 - heightOffset, 3, backgroundColor.getRGB());
        RenderUtils.drawRoundRect(res.getScaledWidth() - offset, res.getScaledHeight() - height - heightOffset, res.getScaledWidth() - offset + 2, res.getScaledHeight() - 10 - heightOffset, 2, ribbonColor.getRGB());

//        fontRenderer.drawString(title, ((int) (res.getScaledWidth() - offset + 8)), (int) ((res.getScaledHeight() - height - heightOffset)) - 3, -1);
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + type.name().toLowerCase(Locale.ROOT) + ".png"), (int) (res.getScaledWidth() - offset) + 4, (int) (res.getScaledHeight() - height - heightOffset) + 2, 10, 10);
        Main.fontLoaders.fonts.get("inter14").drawString(messsage, (int) (res.getScaledWidth() - offset + 22), (int) (res.getScaledHeight() - height - heightOffset) + 3, new Color(0, 0, 0).getRGB());
    }
}
