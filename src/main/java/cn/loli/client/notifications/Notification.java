

package cn.loli.client.notifications;

import cn.loli.client.utils.AnimationUtils;
import cn.loli.client.utils.fontRenderer.GlyphPageFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Notification {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final NotificationType type;
    private final String title;
    private final String messsage;
    private long start;

    private double localHeightOffset;

    private final long fadedIn;
    private final long fadeOut;
    private final long end;
    private final AnimationUtils animationUtils = new AnimationUtils();

    public Notification(NotificationType type, String title, String messsage, int length) {
        this.type = type;
        this.title = title;
        this.messsage = messsage;

        fadedIn = 200L * length;
        fadeOut = fadedIn + 500L * length;
        end = fadeOut + fadedIn;
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(mode, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
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

        Color backgroundColor = new Color(0, 0, 0, 220);
        Color ribbonColor;

        if (type == NotificationType.INFO)
            ribbonColor = new Color(100, 50, 250);
        else if (type == NotificationType.WARNING)
            ribbonColor = new Color(250, 210, 0);
        else {
            ribbonColor = new Color(200, 10, 10);
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
            backgroundColor = new Color(i, 0, 0, 220);
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        double heightOffset = 20 + number * (height + 5);
        localHeightOffset = animationUtils.animate(heightOffset, localHeightOffset, 0.2, 10);
        heightOffset = localHeightOffset;

        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height - heightOffset, res.getScaledWidth(), res.getScaledHeight() - 5 - heightOffset, backgroundColor.getRGB());
        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height - heightOffset, res.getScaledWidth() - offset + 4, res.getScaledHeight() - 5 - heightOffset, ribbonColor.getRGB());

        fontRenderer.drawString(title, ((int) (res.getScaledWidth() - offset + 8)), (int) ((res.getScaledHeight() - height - heightOffset)) - 3, -1);
        fontRenderer.drawString(messsage, (int) (res.getScaledWidth() - offset + 8), (int) (res.getScaledHeight() - 16 - heightOffset), -1);
    }
}
