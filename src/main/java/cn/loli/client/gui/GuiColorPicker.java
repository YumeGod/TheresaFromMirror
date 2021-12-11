

package cn.loli.client.gui;

import cn.loli.client.utils.ColorUtils;
import com.google.common.base.Predicate;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * The GuiScreen for the color picker
 *
 * @author Earthcomputer
 */
public class GuiColorPicker extends GuiScreen {
    private final GuiScreen previousScreen;
    private final IColorPickerCallback callback;

    private GuiButton doneButton;
    private GuiButton cancelButton;
    private GuiTextField hueField;
    private GuiTextField saturationField;
    private GuiTextField valueField;
    private GuiTextField redField;
    private GuiTextField greenField;
    private GuiTextField blueField;
    private GuiTextField alphaField;

    private int hue;
    private int saturation = 100;
    private int value = 100;
    private int rgb = 0xff0000;
    private int alpha = 255;
    private boolean enableAlpha = true;

    /**
     * -1 for nothing, 0 for wheel, 1 for hue, 2 for saturation, 3 for value, 4
     * for red, 5 for green, 6 for blue, 7 for alpha
     */
    private int clicked = -1;

    /**
     * Creates a color picker with the specified callback
     *
     * @param previousScreen
     * @param callback
     */
    public GuiColorPicker(GuiScreen previousScreen, IColorPickerCallback callback) {
        this(previousScreen, callback, true);
    }

    /**
     * Creates a color picker with the specified callback, with enableAlpha
     * specified
     *
     * @param previousScreen
     * @param callback
     * @param enableAlpha
     */
    public GuiColorPicker(GuiScreen previousScreen, IColorPickerCallback callback, boolean enableAlpha) {
        this.previousScreen = previousScreen;
        this.callback = callback;
        this.enableAlpha = enableAlpha;

        int color = callback.getColor().getRGB();
        rgb = color & 0x00ffffff;
        alpha = (color & 0xff000000) >>> 24;

        int[] hsv = ColorUtils.rgbToHsv(rgb);
        hue = hsv[0] == -1 ? 0 : hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    class NumberPredicate implements Predicate<String> {
        private final int max;

        public NumberPredicate(int max) {
            this.max = max;
        }

        @Override
        public boolean apply(String input) {
            try {
                int n = Integer.parseInt(input);
                return n >= 0 && n <= max;
            } catch (NumberFormatException e) {
                return input.isEmpty();
            }
        }
    }

    @Override
    public void initGui() {
        buttonList.add(doneButton = new GuiButton(0, width / 2 - 160, height - 30, 150, 20, I18n.format("gui.done")));
        buttonList.add(cancelButton = new GuiButton(1, width / 2 + 5, height - 30, 150, 20, I18n.format("gui.cancel")));


        hueField = new GuiTextField(0, fontRendererObj, width / 2 + 10, height / 2 - 70, 40, 20);
        hueField.setText(String.valueOf(hue));
        hueField.setValidator(new NumberPredicate(359));
        saturationField = new GuiTextField(0, fontRendererObj, width / 2 + 70, height / 2 - 70, 40, 20);
        saturationField.setText(String.valueOf(saturation));
        saturationField.setValidator(new NumberPredicate(100));
        valueField = new GuiTextField(0, fontRendererObj, width / 2 + 130, height / 2 - 70, 40, 20);
        valueField.setText(String.valueOf(value));
        valueField.setValidator(new NumberPredicate(100));
        redField = new GuiTextField(0, fontRendererObj, width / 2 + 10, height / 2 + 50, 40, 20);
        redField.setText(String.valueOf((rgb & 0xff0000) >> 16));
        redField.setValidator(new NumberPredicate(255));
        greenField = new GuiTextField(0, fontRendererObj, width / 2 + 70, height / 2 + 50, 40, 20);
        greenField.setText(String.valueOf((rgb & 0x00ff00) >> 8));
        greenField.setValidator(new NumberPredicate(255));
        blueField = new GuiTextField(0, fontRendererObj, width / 2 + 130, height / 2 + 50, 40, 20);
        blueField.setText(String.valueOf(rgb & 0x0000ff));
        blueField.setValidator(new NumberPredicate(255));
        alphaField = new GuiTextField(0, fontRendererObj, width / 2 - 190, height / 2 + 37, 40, 20);
        alphaField.setText(String.valueOf(alpha));
        alphaField.setValidator(new NumberPredicate(255));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        String str = "Color Picker";
        drawString(fontRendererObj, str, width / 2 - fontRendererObj.getStringWidth(str) / 2, 12, 0xffffff);

        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        int col1;
        int col2;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer = tessellator.getWorldRenderer();

        // Color wheel
        int x = width / 2 - 80;
        int y = height / 2 - 30;
        for (float f = 0; f < 360; f += 0.25) {
            renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            float fRads = (float) Math.toRadians(f);
            col1 = ColorUtils.hsvToRgb((int) f, 100, 100);
            renderer.pos(x, y, 0).color(1f, 1f, 1f, 1f).endVertex();
            renderer.pos(x + Math.cos(fRads) * 50, y + Math.sin(fRads) * 50, 0)
                    .color((col1 & 0xff0000) >> 16, (col1 & 0x00ff00) >> 8, col1 & 0x0000ff, 255).endVertex();
            tessellator.draw();
        }

        x = width / 2;

        // hue
        y -= 10;
        for (int i = 0; i < 64; i += 8) {
            col1 = ColorUtils.hsvToRgb(i * 360 / 64, 100, 100) | 0xff000000;
            col2 = ColorUtils.hsvToRgb((i + 8) * 360 / 64, 100, 100) | 0xff000000;
            ColorUtils.drawGradientRect(x + i, y, x + i + 8, y + 20, col1, col2, col1, col2);
        }

        // saturation
        y += 30;
        col1 = ColorUtils.hsvToRgb(hue, 0, value) | 0xff000000;
        col2 = ColorUtils.hsvToRgb(hue, 100, value) | 0xff000000;
        ColorUtils.drawGradientRect(x, y, x + 64, y + 20, col1, col2, col1, col2);

        // value
        y += 30;
        col1 = ColorUtils.hsvToRgb(hue, saturation, 100) | 0xff000000;
        ColorUtils.drawGradientRect(x, y, x + 64, y + 20, 0xff000000, col1, 0xff000000, col1);

        // red
        x += 100;
        y = height / 2 - 40;
        col1 = rgb | 0xff000000;
        ColorUtils.drawGradientRect(x, y, x + 64, y + 20, col1 & 0xff00ffff, col1 | 0x00ff0000, col1 & 0xff00ffff,
                col1 | 0x00ff0000);

        // green
        y += 30;
        ColorUtils.drawGradientRect(x, y, x + 64, y + 20, col1 & 0xffff00ff, col1 | 0x0000ff00, col1 & 0xffff00ff,
                col1 | 0x0000ff00);

        // blue
        y += 30;
        ColorUtils.drawGradientRect(x, y, x + 64, y + 20, col1 & 0xffffff00, col1 | 0x000000ff, col1 & 0xffffff00,
                col1 | 0x000000ff);

        if (enableAlpha) {
            // alpha transparent background
            x = width / 2 - 170;
            y = height / 2 - 32;

            ColorUtils.drawGradientRect(x, y, x + 20, y + 64, rgb | 0xff000000, rgb | 0xff000000, rgb, rgb);
        }

        x = width / 2 - 80;
        y = height / 2 + 30;
        if (enableAlpha && alpha != 255) {
            // color transparent background
            GlStateManager.enableTexture2D();
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            renderer.pos(x - 30, y + 20, 0).tex(0, 20f / 16).endVertex();
            renderer.pos(x + 30, y + 20, 0).tex(60f / 16, 20f / 16).endVertex();
            renderer.pos(x + 30, y, 0).tex(60f / 16, 0).endVertex();
            renderer.pos(x - 30, y, 0).tex(0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableTexture2D();
        }
        // color
        Gui.drawRect(x - 30, y, x + 30, y + 20, col1);

        GlStateManager.tryBlendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableTexture2D();
        x = width / 2;
        // hue marker
        y = height / 2 - 40;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + hue * 64 / 360, y, 0).endVertex();
        renderer.pos(x + hue * 64 / 360, y + 20, 0).endVertex();
        tessellator.draw();

        // saturation marker
        y += 30;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + saturation * 64 / 100, y, 0).endVertex();
        renderer.pos(x + saturation * 64 / 100, y + 20, 0).endVertex();
        tessellator.draw();

        // value marker
        y += 30;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + value * 64 / 100, y, 0).endVertex();
        renderer.pos(x + value * 64 / 100, y + 20, 0).endVertex();
        tessellator.draw();

        // red marker
        x += 100;
        y = height / 2 - 40;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + ((rgb & 0xff0000) >> 16) * 64 / 255, y, 0).endVertex();
        renderer.pos(x + ((rgb & 0xff0000) >> 16) * 64 / 255, y + 20, 0).endVertex();
        tessellator.draw();

        // green marker
        y += 30;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + ((rgb & 0x00ff00) >> 8) * 64 / 255, y, 0).endVertex();
        renderer.pos(x + ((rgb & 0x00ff00) >> 8) * 64 / 255, y + 20, 0).endVertex();
        tessellator.draw();

        // blue marker
        y += 30;
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(x + (rgb & 0x0000ff) * 64 / 255, y, 0).endVertex();
        renderer.pos(x + (rgb & 0x0000ff) * 64 / 255, y + 20, 0).endVertex();
        tessellator.draw();

        if (enableAlpha) {
            // alpha marker
            x = width / 2 - 170;
            y = height / 2 - 32;
            renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
            renderer.pos(x, y + 64 - alpha * 64 / 255, 0).endVertex();
            renderer.pos(x + 20, y + 64 - alpha * 64 / 255, 0).endVertex();
            tessellator.draw();
        }

        GlStateManager.enableTexture2D();

        // color wheel marker
        x = width / 2 - 80;
        y = height / 2 - 30;
        int dist = saturation / 2;
        mc.getTextureManager().bindTexture(Gui.icons);
        this.drawTexturedModalRect(x + (int) (Math.cos(Math.toRadians(hue)) * dist - 7),
                y + (int) (Math.sin(Math.toRadians(hue)) * dist) - 7, 0, 0, 16, 16);

        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        // labels
        x = width / 2;
        y -= 10;
        drawString(fontRendererObj, "H:", x - 10, y + 5, 0xffffff);
        drawString(fontRendererObj, "R:", x + 90, y + 5, 0xffffff);
        y += 30;
        drawString(fontRendererObj, "S:", x - 10, y + 5, 0xffffff);
        drawString(fontRendererObj, "G:", x + 90, y + 5, 0xffffff);
        y += 30;
        drawString(fontRendererObj, "V:", x - 10, y + 5, 0xffffff);
        drawString(fontRendererObj, "B:", x + 90, y + 5, 0xffffff);

        y = height / 2 - 65;
        drawString(fontRendererObj, "H:", width / 2, y, 0xffffff);
        drawString(fontRendererObj, "S:", width / 2 + 60, y, 0xffffff);
        drawString(fontRendererObj, "V:", width / 2 + 120, y, 0xffffff);
        y = height / 2 + 55;
        drawString(fontRendererObj, "R:", width / 2, y, 0xffffff);
        drawString(fontRendererObj, "G:", width / 2 + 60, y, 0xffffff);
        drawString(fontRendererObj, "B:", width / 2 + 120, y, 0xffffff);

        if (enableAlpha) {
            str = "Opacity";
            x = width / 2 - 150 - fontRendererObj.getStringWidth(str);
            y = height / 2 - 45;
            drawString(fontRendererObj, str, x, y, 0xffffff);
        }

        hueField.drawTextBox();
        saturationField.drawTextBox();
        valueField.drawTextBox();
        redField.drawTextBox();
        greenField.drawTextBox();
        blueField.drawTextBox();
        if (enableAlpha)
            alphaField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            actionPerformed(cancelButton);
            return;
        } else if (keyCode == Keyboard.KEY_RETURN) {
            actionPerformed(doneButton);
            return;
        }

        hueField.textboxKeyTyped(typedChar, keyCode);
        saturationField.textboxKeyTyped(typedChar, keyCode);
        valueField.textboxKeyTyped(typedChar, keyCode);
        redField.textboxKeyTyped(typedChar, keyCode);
        greenField.textboxKeyTyped(typedChar, keyCode);
        blueField.textboxKeyTyped(typedChar, keyCode);
        if (enableAlpha)
            alphaField.textboxKeyTyped(typedChar, keyCode);

        // 0 for h, s or v, 1 for r, g or b
        int typed = -1;
        int old;
        boolean doneEnabled = true;
        try {
            old = hue;
            hue = Integer.parseInt(hueField.getText());
            if (hue != old)
                typed = 0;
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        try {
            old = saturation;
            saturation = Integer.parseInt(saturationField.getText());
            if (saturation != old)
                typed = 0;
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        try {
            old = value;
            value = Integer.parseInt(valueField.getText());
            if (value != old)
                typed = 0;
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        int i;
        old = rgb;
        try {
            i = Integer.parseInt(redField.getText());
            rgb &= 0x00ffff;
            rgb |= i << 16;
            if (rgb != old) {
                old = rgb;
                typed = 1;
            }
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        try {
            i = Integer.parseInt(greenField.getText());
            rgb &= 0xff00ff;
            rgb |= i << 8;
            if (rgb != old) {
                old = rgb;
                typed = 1;
            }
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        try {
            i = Integer.parseInt(blueField.getText());
            rgb &= 0xffff00;
            rgb |= i;
            if (rgb != old) {
                typed = 1;
            }
        } catch (NumberFormatException e) {
            doneEnabled = false;
        }
        if (enableAlpha) {
            try {
                alpha = Integer.parseInt(alphaField.getText());
            } catch (NumberFormatException e) {
                doneEnabled = false;
            }
        }
        if (typed == 0) {
            rgb = ColorUtils.hsvToRgb(hue, saturation, value);
            redField.setText(String.valueOf((rgb & 0xff0000) >> 16));
            greenField.setText(String.valueOf((rgb & 0x00ff00) >> 8));
            blueField.setText(String.valueOf(rgb & 0x0000ff));
        } else if (typed == 1) {
            int[] hsv = ColorUtils.rgbToHsv(rgb);
            if (hsv[0] != -1)
                hue = hsv[0];
            saturation = hsv[1];
            value = hsv[2];
            if (hsv[0] != -1)
                hueField.setText(String.valueOf(hue));
            saturationField.setText(String.valueOf(saturation));
            valueField.setText(String.valueOf(value));
        }
        doneButton.enabled = doneEnabled;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            int hPrev = hue, sPrev = saturation, vPrev = value, rgbPrev = rgb, aPrev = alpha;
            int x = width / 2 - 80;
            int y = height / 2 - 30;
            int dx = mouseX - x;
            int dy = mouseY - y;
            if (dx * dx + dy * dy <= 50 * 50) {
                clicked = 0;
                updateWheelClick(dx, dy);
            }

            x = width / 2;
            y -= 10;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 1;
                updateHueClick(mouseX - x);
            }

            y += 30;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 2;
                updateSaturationClick(mouseX - x);
            }
            y += 30;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 3;
                updateValueClick(mouseX - x);
            }
            x += 100;
            y = height / 2 - 40;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 4;
                updateRedClick(mouseX - x);
            }
            y += 30;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 5;
                updateGreenClick(mouseX - x);
            }
            y += 30;
            if (mouseX >= x && mouseX < x + 64 && mouseY >= y && mouseY < y + 20) {
                clicked = 6;
                updateBlueClick(mouseX - x);
            }

            if (enableAlpha) {
                x = width / 2 - 170;
                y = height / 2 - 32;
                if (mouseX >= x && mouseX < x + 20 && mouseY >= y && mouseY < y + 64) {
                    clicked = 7;
                    updateAlphaClick(mouseY - y);
                }
            }

            if (hue != hPrev)
                hueField.setText(String.valueOf(hue));
            if (saturation != sPrev)
                saturationField.setText(String.valueOf(saturation));
            if (value != vPrev)
                valueField.setText(String.valueOf(value));
            if ((rgb & 0xff0000) != (rgbPrev & 0xff0000))
                redField.setText(String.valueOf((rgb & 0xff0000) >> 16));
            if ((rgb & 0x00ff00) != (rgbPrev & 0x00ff00))
                greenField.setText(String.valueOf((rgb & 0x00ff00) >> 8));
            if ((rgb & 0x0000ff) != (rgbPrev & 0x0000ff))
                blueField.setText(String.valueOf(rgb & 0x0000ff));
            if (alpha != aPrev)
                alphaField.setText(String.valueOf(alpha));
        }
        hueField.mouseClicked(mouseX, mouseY, mouseButton);
        saturationField.mouseClicked(mouseX, mouseY, mouseButton);
        valueField.mouseClicked(mouseX, mouseY, mouseButton);
        redField.mouseClicked(mouseX, mouseY, mouseButton);
        greenField.mouseClicked(mouseX, mouseY, mouseButton);
        blueField.mouseClicked(mouseX, mouseY, mouseButton);
        if (enableAlpha)
            alphaField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0)
            clicked = -1;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        int hPrev = hue, sPrev = saturation, vPrev = value, rgbPrev = rgb, aPrev = alpha;
        int hsvBoxesLeft = width / 2;
        int rgbBoxesLeft = hsvBoxesLeft + 100;
        switch (clicked) {
            case 0:
                updateWheelClick(mouseX - (width / 2 - 80), mouseY - (height / 2 - 30));
                break;
            case 1:
                updateHueClick(mouseX - hsvBoxesLeft);
                break;
            case 2:
                updateSaturationClick(mouseX - hsvBoxesLeft);
                break;
            case 3:
                updateValueClick(mouseX - hsvBoxesLeft);
                break;
            case 4:
                updateRedClick(mouseX - rgbBoxesLeft);
                break;
            case 5:
                updateGreenClick(mouseX - rgbBoxesLeft);
                break;
            case 6:
                updateBlueClick(mouseX - rgbBoxesLeft);
                break;
            case 7:
                updateAlphaClick(mouseY - (height / 2 - 32));
                break;
        }
        if (hue != hPrev)
            hueField.setText(String.valueOf(hue));
        if (saturation != sPrev)
            saturationField.setText(String.valueOf(saturation));
        if (value != vPrev)
            valueField.setText(String.valueOf(value));
        if ((rgb & 0xff0000) != (rgbPrev & 0xff0000))
            redField.setText(String.valueOf((rgb & 0xff0000) >> 16));
        if ((rgb & 0x00ff00) != (rgbPrev & 0x00ff00))
            greenField.setText(String.valueOf((rgb & 0x00ff00) >> 8));
        if ((rgb & 0x0000ff) != (rgbPrev & 0x0000ff))
            blueField.setText(String.valueOf(rgb & 0x0000ff));
        if (alpha != aPrev)
            alphaField.setText(String.valueOf(alpha));
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                callback.setColor(new Color((alpha << 24) | rgb, true));
                mc.displayGuiScreen(previousScreen);
                break;
            case 1:
                mc.displayGuiScreen(previousScreen);
                break;
        }
    }

    private void updateWheelClick(int dx, int dy) {
        hue = (int) Math.toDegrees(Math.atan2(dy, dx));
        if (hue < 0)
            hue += 360;

        int dist = (int) Math.sqrt(dx * dx + dy * dy);
        if (dist > 50)
            dist = 50;
        saturation = dist * 2;

        rgb = ColorUtils.hsvToRgb(hue, saturation, value);
    }

    private void updateHueClick(int dx) {
        hue = MathHelper.clamp_int(dx * 360 / 64, 0, 360);

        rgb = ColorUtils.hsvToRgb(hue, saturation, value);
    }

    private void updateSaturationClick(int dx) {
        saturation = MathHelper.clamp_int(dx * 100 / 64, 0, 100);

        rgb = ColorUtils.hsvToRgb(hue, saturation, value);
    }

    private void updateValueClick(int dx) {
        value = MathHelper.clamp_int(dx * 100 / 64, 0, 100);

        rgb = ColorUtils.hsvToRgb(hue, saturation, value);
    }

    private void updateRedClick(int dx) {
        dx = MathHelper.clamp_int(dx, 0, 64);
        rgb &= 0x00ffff;
        rgb |= (dx * 255 / 64) << 16;
        int[] hsv = ColorUtils.rgbToHsv(rgb);
        if (hsv[0] != -1)
            hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    private void updateGreenClick(int dx) {
        dx = MathHelper.clamp_int(dx, 0, 64);
        rgb &= 0xff00ff;
        rgb |= (dx * 255 / 64) << 8;
        int[] hsv = ColorUtils.rgbToHsv(rgb);
        if (hsv[0] != -1)
            hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    private void updateBlueClick(int dx) {
        dx = MathHelper.clamp_int(dx, 0, 64);
        rgb &= 0xffff00;
        rgb |= dx * 255 / 64;
        int[] hsv = ColorUtils.rgbToHsv(rgb);
        if (hsv[0] != -1)
            hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    private void updateAlphaClick(int dy) {
        dy = MathHelper.clamp_int(dy, 0, 64);
        dy = 64 - dy;
        alpha = dy * 255 / 64;
    }
}