package cn.loli.client.gui.clickui.dropdown;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.dropdown.panels.CategoryPanel;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Panel {
    public String name;
    public double x;
    public double y;
    public double width;
    public double height;
    public boolean visible;
    public boolean hover;
    public boolean click;
    public boolean drag;
    public double dragX;
    public double dragY;
    public boolean drag1;
    public double dragY1;

    public float scroll;
    public float scroll_temp;

    public double TITLE_HEIGHT = 24.0;
    public double scroll_height;
    public double maxHeight;

    public Panel(String name) {
        this.name = name;
        this.visible = true;
        this.hover = false;
        this.click = false;
        this.drag = false;
        this.dragX = 0;
        this.dragY = 0;
    }

    public void update(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Don't override this method
    public void draw(int mouseX, int mouseY, float partialTicks, int mouseDWheel) {
        onMouse(mouseX, mouseY, 0);
        RenderUtils.drawRect(x, y, x + width, y + TITLE_HEIGHT, new Color(255, 255, 255).getRGB());
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(247, 247, 247).getRGB());
        RenderUtils.drawGradientRect((float) x, (float) (y + TITLE_HEIGHT), (float) (x + width), (float) (y + TITLE_HEIGHT + 5), new Color(150, 150, 150, 0).getRGB(), new Color(150, 150, 150, 70).getRGB());
        HFontRenderer titleFont = Main.INSTANCE.fontLoaders.fonts.get("roboto22");
        RenderUtils.drawRect(x, y, x + width, y + TITLE_HEIGHT, new Color(255, 255, 255).getRGB());
        if (this instanceof CategoryPanel) {
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + name.toLowerCase() + ".png"), (float) (x + width / 2) - titleFont.getStringWidth(name) / 2f - 8, y + 10, 8, 8, new Color(61, 61, 61));
        }
        titleFont.drawString(name, (float) (x + width / 2) - titleFont.getStringWidth(name) / 2f + 5, (float) (y + 10), new Color(61, 61, 61).getRGB());

        RenderUtils.drawRect(x + 40, y + height - 6, x + width - 40, y + height - 5, new Color(153, 153, 153).getRGB());


        scroll = AnimationUtils.smoothAnimation(scroll, scroll_temp, 50f, 0.3f);
        double mn = (maxHeight - TITLE_HEIGHT - 10) / 25;
        double slider_height = ((height - TITLE_HEIGHT - 10) / (mn * 25)) * (height - TITLE_HEIGHT - 10);
        boolean flag = y + TITLE_HEIGHT + (Math.abs(scroll) / (mn * 25)) * (height - TITLE_HEIGHT - 20) + slider_height < y + height - 10;
        if (isHovered(x, x + width, y + TITLE_HEIGHT, y + height - 10, mouseX, mouseY)) {
            if (mouseDWheel > 0) {
                scroll_temp += 8;
                if (scroll_temp > 0) scroll_temp = 0;
            } else if (mouseDWheel < 0 && flag) {
                scroll_temp -= 8;
            }
        }
        if (drag1 && (mouseY - dragY1 - y <= mouseY) && (mouseY - dragY1 - y) <= (this.y - this.TITLE_HEIGHT - 10 + scroll + maxHeight)) {
            this.height = mouseY - dragY1 - y;
        }
        // 绘制滑动条
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor((float) x, (float) (y + TITLE_HEIGHT), (float) width, (float) (height - TITLE_HEIGHT - 10));
        RenderUtils.drawRect(x + width - 5, y + TITLE_HEIGHT + (Math.abs(scroll) / (mn * 25)) * (height - TITLE_HEIGHT - 20), x + width - 4, y + TITLE_HEIGHT + (Math.abs(scroll) / (mn * 25)) * (height - TITLE_HEIGHT - 20) + slider_height, new Color(208, 208, 208).getRGB());
        display(mouseX, mouseY, partialTicks, mouseDWheel);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (height > maxHeight) {
            height = maxHeight;
        }
        if (height < 10) {
            height = 10;
        }
    }

    // Override this method
    public void display(int mouseX, int mouseY, float partialTicks, int mouseDWheel) {

    }

    public void onClicked(double mx, double my, int mouseButton) {

    }

    public void onClick(double mx, double my, int mouseButton) {
        if (!isHovered(x, x + width, y, y + height, mx, my)) {
            return;
        }
        onClicked(mx, my, mouseButton);
        if (isHovered(x, x + width, y, y + TITLE_HEIGHT, mx, my)) {
            drag = true;
            dragX = mx - x;
            dragY = my - y;
        }

        if (isHovered(x, x + width, y + height - 8, y + height, mx, my)) {
            drag1 = true;
            dragY1 = my - (y + height);
        }
    }

    public void onMouse(double mx, double my, int btn) {
        if (!Mouse.isButtonDown(0)) {
            drag = false;
            drag1 = false;
            dragX = 0;
            dragY = 0;
        }
        if (drag) {
            this.x = mx - dragX;
            this.y = my - dragY;
        }

    }

    public boolean isHovered(double x, double x1, double y, double y1, double mouseX, double mouseY) {
        if (x <= mouseX && x1 >= mouseX && y <= mouseY && y1 >= mouseY) {
            return true;
        }
        return false;
    }


}
