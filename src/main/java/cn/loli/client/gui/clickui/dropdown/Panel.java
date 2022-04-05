package cn.loli.client.gui.clickui.dropdown;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.utils.render.RenderUtils;
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
    public boolean dragable;
    public boolean dragStart;
    public double dragX;
    public double dragY;

    public double TITLE_HEIGHT = 20.0;

    public Panel(String name) {
        this.name = name;
        this.visible = true;
        this.hover = false;
        this.click = false;
        this.drag = false;
        this.dragable = false;
        this.dragStart = false;
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
    public void draw(int mouseX, int mouseY, float partialTicks) {
        onMouse(mouseX, mouseY, 0);
        RenderUtils.drawRect(x, y, x + width, y + TITLE_HEIGHT, new Color(255, 255, 255).getRGB());
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(247, 247, 247).getRGB());
        HFontRenderer titleFont = Main.INSTANCE.fontLoaders.fonts.get("roboto22");
        RenderUtils.drawRect(x, y, x + width, y + TITLE_HEIGHT, new Color(255, 255, 255).getRGB());
        titleFont.drawString(name, (float) (x + width / 2) - titleFont.getStringWidth(name) / 2f, (float) (y + 8), new Color(61, 61, 61).getRGB());

        RenderUtils.drawRect(x + 40, y + height - 6, x + width - 40, y + height - 5, new Color(153, 153, 153).getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.doGlScissor((float) x, (float) (y + TITLE_HEIGHT), (float) width, (float) (height - TITLE_HEIGHT));
        display(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    // Override this method
    public void display(int mouseX, int mouseY, float partialTicks) {

    }

    public void onClick(double mx, double my, int mouseButton) {
        if (isHovered(x, x + width, y, y + TITLE_HEIGHT, mx, my)) {
            drag = true;
            dragX = mx - x;
            dragY = my - y;
        }
    }

    public void onMouse(double mx, double my, int btn) {
        if (!Mouse.isButtonDown(0)) {
            drag = false;
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
