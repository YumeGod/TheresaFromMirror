package cn.loli.client.utils.render;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.utils.Utils;
import cn.loli.client.utils.misc.timer.TimeHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils extends Utils {
    static int animationX;
    private static final TimeHelper animationTimer = new TimeHelper();

    public static void drawWolframEntityESP(EntityLivingBase entity, int rgb, double posX, double posY, double posZ) {
        GL11.glPushMatrix();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotatef(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        setColor(rgb);
        enableGL3D(1.0F);
        Cylinder c = new Cylinder();
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        c.setDrawStyle(100011);
        c.draw(0.5F, 0.5F, entity.height + 0.1F, 18, 1);
        disableGL3D();
        GL11.glPopMatrix();
    }


    private static void enableGL3D(float lineWidth) {
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glLineWidth(lineWidth);
    }

    private static void disableGL3D() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    private static void setColor(int colorHex) {
        float alpha = (float) (colorHex >> 24 & 255) / 255.0F;
        float red = (float) (colorHex >> 16 & 255) / 255.0F;
        float green = (float) (colorHex >> 8 & 255) / 255.0F;
        float blue = (float) (colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha == 0.0F ? 1.0F : alpha);
    }

    public static void drawBlockESP(double x, double y, double z, int mainColor, int borderColor, float alpha, float lineWidth) {
        float red = (float) (mainColor >> 16 & 255) / 255.0f;
        float green = (float) (mainColor >> 8 & 255) / 255.0f;
        float blue = (float) (mainColor & 255) / 255.0f;

        float lineRed = (float) (borderColor >> 16 & 255) / 255.0f;
        float lineGreen = (float) (borderColor >> 8 & 255) / 255.0f;
        float lineBlue = (float) (borderColor & 255) / 255.0f;

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);

        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        GL11.glLineWidth(lineWidth);
        GL11.glColor4f(lineRed, lineGreen, lineBlue, alpha);
        drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));

        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }


    private static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    private static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawOutlinedRect(int x, int y, int width, int height, int lineSize, Color lineColor, Color backgroundColor) {
        drawRect(x, y, width, height, backgroundColor.getRGB());
        drawRect(x, y, width, y + lineSize, lineColor.getRGB());
        drawRect(x, height - lineSize, width, height, lineColor.getRGB());
        drawRect(x, y + lineSize, x + lineSize, height - lineSize, lineColor.getRGB());
        drawRect(width - lineSize, y + lineSize, width, height - lineSize, lineColor.getRGB());
    }

    public static void drawOutlinedRect(float x, float y, float width, float height, float lineSize, int lineColor) {
        RenderUtils.drawRect(x, y, width, y + lineSize, lineColor);
        RenderUtils.drawRect(x, height - lineSize, width, height, lineColor);
        RenderUtils.drawRect(x, y + lineSize, x + lineSize, height - lineSize, lineColor);
        RenderUtils.drawRect(width - lineSize, y + lineSize, width, height - lineSize, lineColor);
    }

    public static void color(int color) {
        float f = (float) (color >> 24 & 255) / 255.0f;
        float f1 = (float) (color >> 16 & 255) / 255.0f;
        float f2 = (float) (color >> 8 & 255) / 255.0f;
        float f3 = (float) (color & 255) / 255.0f;
        GL11.glColor4f(f1, f2, f3, f);
    }

    public static void drawRoundRect(double xPosition, double yPosition, double endX, double endY, int radius, int color) {
        double width = endX - xPosition;
        double height = endY - yPosition;

        drawRect(xPosition + radius, yPosition + radius, (xPosition + width - radius), (yPosition + height - radius), color);
        drawRect(xPosition, yPosition + radius, xPosition + radius, (yPosition + height - radius), color);
        drawRect((xPosition + width - radius), yPosition + radius, (xPosition + width), (yPosition + height - radius), color);
        drawRect(xPosition + radius, yPosition, (xPosition + width - radius), yPosition + radius, color);
        drawRect(xPosition + radius, (yPosition + height - radius), (xPosition + width - radius), (yPosition + height), color);

        drawFilledCircle(xPosition + radius, yPosition + radius, radius, color, 1);
        drawFilledCircle(xPosition + radius, (yPosition + height - radius), radius, color, 2);
        drawFilledCircle((xPosition + width - radius), yPosition + radius, radius, color, 3);
        drawFilledCircle((xPosition + width - radius), (yPosition + height - radius), radius, color, 4);
        drawFilledCircle(xPosition + radius, yPosition + radius, radius, color, 1);
        drawFilledCircle(xPosition + radius, (yPosition + height - radius), radius, color, 2);
        drawFilledCircle((xPosition + width - radius), yPosition + radius, radius, color, 3);
        drawFilledCircle((xPosition + width - radius), (yPosition + height - radius), radius, color, 4);

        drawRect(0, 0, 0, 0, 0);
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

    public static void drawFilledCircle(double x, double y, double r, int c, int id) {
        GL11.glEnable(GL_POLYGON_SMOOTH);
        float f = (float) (c >> 24 & 0xff) / 255F;
        float f1 = (float) (c >> 16 & 0xff) / 255F;
        float f2 = (float) (c >> 8 & 0xff) / 255F;
        float f3 = (float) (c & 0xff) / 255F;
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glColor4f(f1, f2, f3, f);
        glBegin(GL_POLYGON);
        if (id == 1) {
            glVertex2d(x, y);
            for (int i = 0; i <= 90; i++) {
                double x2 = Math.sin((i * Math.PI / 180)) * r;
                double y2 = Math.cos((i * Math.PI / 180)) * r;
                glVertex2d(x - x2, y - y2);
            }
        } else if (id == 2) {
            glVertex2d(x, y);
            for (int i = 90; i <= 180; i++) {
                double x2 = Math.sin((i * Math.PI / 180)) * r;
                double y2 = Math.cos((i * Math.PI / 180)) * r;
                glVertex2d(x - x2, y - y2);
            }
        } else if (id == 3) {
            glVertex2d(x, y);
            for (int i = 270; i <= 360; i++) {
                double x2 = Math.sin((i * Math.PI / 180)) * r;
                double y2 = Math.cos((i * Math.PI / 180)) * r;
                glVertex2d(x - x2, y - y2);
            }
        } else if (id == 4) {
            glVertex2d(x, y);
            for (int i = 180; i <= 270; i++) {
                double x2 = Math.sin((i * Math.PI / 180)) * r;
                double y2 = Math.cos((i * Math.PI / 180)) * r;
                glVertex2d(x - x2, y - y2);
            }
        } else {
            for (int i = 0; i <= 360; i++) {
                double x2 = Math.sin((i * Math.PI / 180)) * r;
                double y2 = Math.cos((i * Math.PI / 180)) * r;
                glVertex2f((float) (x - x2), (float) (y - y2));
            }
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        Gui.drawRect(0, 0, 0, 0, 0);
        GL11.glDisable(GL_POLYGON_SMOOTH);
    }

    public static void drawImage(final ResourceLocation image, double x, double y, double width, double height) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(image);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawImage(final ResourceLocation image, double x, double y, double width, double height, Color color) {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        mc.getTextureManager().bindTexture(image);
        drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawModalRectWithCustomSizedTexture(double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {
        double f = 1.0F / textureWidth;
        double f1 = 1.0F / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) width) * f, (v + (float) height) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) width) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void doGlScissor(float x, float y, float width, float height) {

        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }

        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320
                && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }

        GL11.glScissor((int) (x * scaleFactor), (int) (mc.displayHeight - (y + height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

    public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
        enableGL2D();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        glColor(topColor);
        GL11.glVertex2f(x, y1);
        GL11.glVertex2f(x1, y1);
        glColor(bottomColor);
        GL11.glVertex2f(x1, y);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        disableGL2D();
    }


    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }


    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }


    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float) c.getRed();
        float g = 0.003921569f * (float) c.getGreen();
        float b = 0.003921569f * (float) c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }

    public static void draw2DESP(EntityLivingBase entity, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        final float partialTicks = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX();
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY();
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
        float SCALE = 0.035f;
        SCALE /= 2.0f;
        GlStateManager.translate((float) x, (float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f), (float) z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glScalef(-SCALE, -SCALE, -SCALE);
        final double xLeft = -30.0;
        final double xRight = 30.0;
        final double yUp = 15.0;
        final double yDown = 140.0;
        drawRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, color);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GlStateManager.disableBlend();
        GL11.glDisable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glNormal3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    public static void renderBox(EntityLivingBase entity, int color) {
        final float partialTicks = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;

        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * partialTicks
                - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX();
        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * partialTicks
                - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY();
        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * partialTicks
                - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
        double width = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX - 0.1;
        double height = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY
                + 0.25;

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        color(entity.hurtTime > 1 ? reAlpha(new Color(199, 35, 35).getRGB(), 0.2f) : color);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth(1);
        GL11.glColor4f(1f,
                1f, 1f, 0);
        drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawRectBound(float left, float top, float width, float height, int color) {
        drawRect(left, top, left + width, top + height, color);
    }

    public static void roundedRect(float x, float y, float width, float height, float edgeRadius, int color, float borderWidth, int borderColor) {
        if (edgeRadius < 0.0F) {
            edgeRadius = 0.0F;
        }

        if (edgeRadius > width / 2.0F) {
            edgeRadius = width / 2.0F;
        }

        if (edgeRadius > height / 2.0F) {
            edgeRadius = height / 2.0F;
        }

        drawRectBound(x + edgeRadius, y + edgeRadius, width - edgeRadius * 2.0F, height - edgeRadius * 2.0F, color);
        drawRectBound(x + edgeRadius, y, width - edgeRadius * 2.0F, edgeRadius, color);
        drawRectBound(x + edgeRadius, y + height - edgeRadius, width - edgeRadius * 2.0F, edgeRadius, color);
        drawRectBound(x, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0F, color);
        drawRectBound(x + width - edgeRadius, y + edgeRadius, edgeRadius, height - edgeRadius * 2.0F, color);
        GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(1.0F);

        color(color);
        GL11.glBegin(6);
        float centerX = x + edgeRadius;
        float centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        int vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

        int i;
        double angleRadians;
        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = 6.283185307179586D * (double) (i + 90) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glEnd();
        GL11.glBegin(6);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = 6.283185307179586D * (double) (i + 270) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }
        GL11.glEnd();

        GL11.glBegin(6);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;
        GL11.glVertex2d(centerX, centerY);
        vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

        for (i = 0; i < vertices + 1; ++i) {
            angleRadians = 6.283185307179586D * (double) i / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }
        GL11.glEnd();

        RenderUtils.color(borderColor);
        GL11.glLineWidth(borderWidth);
        GL11.glBegin(3);
        centerX = x + edgeRadius;
        centerY = y + edgeRadius;
        vertices = (int) Math.min(Math.max(edgeRadius, 10.0F), 90.0F);

        for (i = vertices; i >= 0; --i) {
            angleRadians = 6.283185307179586D * (double) (i + 180) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glVertex2d(x + edgeRadius, y);
        GL11.glVertex2d(x + width - edgeRadius, y);
        centerX = x + width - edgeRadius;
        centerY = y + edgeRadius;

        for (i = vertices; i >= 0; --i) {
            angleRadians = 6.283185307179586D * (double) (i + 90) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glVertex2d(x + width, y + edgeRadius);
        GL11.glVertex2d(x + width, y + height - edgeRadius);
        centerX = x + width - edgeRadius;
        centerY = y + height - edgeRadius;

        for (i = vertices; i >= 0; --i) {
            angleRadians = 6.283185307179586D * (double) i / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glVertex2d(x + width - edgeRadius, y + height);
        GL11.glVertex2d(x + edgeRadius, y + height);
        centerX = x + edgeRadius;
        centerY = y + height - edgeRadius;

        for (i = vertices; i >= 0; --i) {
            angleRadians = 6.283185307179586D * (double) (i + 270) / (double) (vertices * 4);
            GL11.glVertex2d((double) centerX + Math.sin(angleRadians) * (double) edgeRadius, (double) centerY + Math.cos(angleRadians) * (double) edgeRadius);
        }

        GL11.glVertex2d(x, y + height - edgeRadius);
        GL11.glVertex2d(x, y + edgeRadius);
        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.shadeModel(7424);

        GlStateManager.resetColor();
    }

    public static void drawRoundedRect(float left, float top, float width, float height, float radius, int color) {
        float right = left + width;
        float bottom = top + height;

        final int semicircle = 18;
        final float f = 90.0f / semicircle;
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        color(color);
        GL11.glBegin(5);
        GL11.glVertex2f(left + radius, top);
        GL11.glVertex2f(left + radius, bottom);
        GL11.glVertex2f(right - radius, top);
        GL11.glVertex2f(right - radius, bottom);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(left, top + radius);
        GL11.glVertex2f(left + radius, top + radius);
        GL11.glVertex2f(left, bottom - radius);
        GL11.glVertex2f(left + radius, bottom - radius);
        GL11.glEnd();
        GL11.glBegin(5);
        GL11.glVertex2f(right, top + radius);
        GL11.glVertex2f(right - radius, top + radius);
        GL11.glVertex2f(right, bottom - radius);
        GL11.glVertex2f(right - radius, bottom - radius);
        GL11.glEnd();
        GL11.glBegin(6);
        float f6 = right - radius;
        float f7 = top + radius;
        GL11.glVertex2f(f6, f7);
        int j = 0;
        for (j = 0; j <= semicircle; ++j) {
            final float f8 = j * f;
            GL11.glVertex2d((f6 + radius * Math.cos(Math.toRadians(f8))),
                    (f7 - radius * Math.sin(Math.toRadians(f8))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = left + radius;
        f7 = top + radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= semicircle; ++j) {
            final float f9 = j * f;
            GL11.glVertex2d((f6 - radius * Math.cos(Math.toRadians(f9))),
                    (f7 - radius * Math.sin(Math.toRadians(f9))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = left + radius;
        f7 = bottom - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= semicircle; ++j) {
            final float f10 = j * f;
            GL11.glVertex2d((f6 - radius * Math.cos(Math.toRadians(f10))),
                    (f7 + radius * Math.sin(Math.toRadians(f10))));
        }
        GL11.glEnd();
        GL11.glBegin(6);
        f6 = right - radius;
        f7 = bottom - radius;
        GL11.glVertex2f(f6, f7);
        for (j = 0; j <= semicircle; ++j) {
            final float f11 = j * f;
            GL11.glVertex2d((f6 + radius * Math.cos(Math.toRadians(f11))),
                    (f7 + radius * Math.sin(Math.toRadians(f11))));
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static Color getRainbow(int offset, int speed, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offset) % speed) / (float) speed;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public static void drawIcarusESP(EntityLivingBase target, Color color, boolean rainbow) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        float animationY = 0.0f;
        float animationY2 = 0.0f;
        int currentOffset = 0;
        if (animationTimer.hasReached(10)) {
            animationX++;
            animationTimer.reset();
        }

        translateRotate(target);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = animationX; i < 100 + animationX; i++) {
            final double c = (2 * i * Math.PI / 100);
            Color pick = rainbow ? getRainbow(currentOffset, 1000, (float) 0.8, 1f) : color;
            GL11.glColor3d(pick.getRed() / 255f, pick.getGreen() / 255f, pick.getBlue() / 255f);
            GL11.glVertex3d((Math.cos(c) * 0.5), animationY, (Math.sin(c) * 0.5));
            animationY += target.height / 100;
            currentOffset += 10;
        }
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 50 + animationX; i < 150 + animationX; i++) {
            final double c = (2 * i * Math.PI / 100);
            Color pick = rainbow ? getRainbow(currentOffset, 1000, (float) 0.8, 1f) : color;
            GL11.glColor3d(pick.getRed() / 255f, pick.getGreen() / 255f, pick.getBlue() / 255f);
            GL11.glVertex3d((Math.cos(c) * 0.5), animationY2, (Math.sin(c) * 0.5));
            animationY2 += target.height / 100;
            currentOffset += 10;
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private static void translateRotate(Entity entity) {
        final float partialTicks = ((IAccessorMinecraft) mc).getTimer().renderPartialTicks;
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX();
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY();
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ();
        GL11.glTranslated(x, y, z);
        GL11.glNormal3d(0.0, 1.0, 0.0);
        GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = clamp(alpha, 0, 255) << 24;
        color |= clamp(red, 0, 255) << 16;
        color |= clamp(green, 0, 255) << 8;
        color |= clamp(blue, 0, 255);

        return color;
    }

    public static int darker(int hexColor, int factor) {
        float alpha = (float) (hexColor >> 24 & 255);
        float red = Math.max((float) (hexColor >> 16 & 255) - (float) (hexColor >> 16 & 255) / (100.0F / (float) factor), 0.0F);
        float green = Math.max((float) (hexColor >> 8 & 255) - (float) (hexColor >> 8 & 255) / (100.0F / (float) factor), 0.0F);
        float blue = Math.max((float) (hexColor & 255) - (float) (hexColor & 255) / (100.0F / (float) factor), 0.0F);
        return (int) ((float) (((int) alpha << 24) + ((int) red << 16) + ((int) green << 8)) + blue);
    }

    public static void drawScaledCustomSizeModalRect(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }
}
