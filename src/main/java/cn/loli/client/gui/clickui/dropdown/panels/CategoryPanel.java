package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.AnimationUtils;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class CategoryPanel extends Panel {
    private ModuleCategory category;
    private float scroll;
    private float scroll_temp;


    public CategoryPanel(String name, ModuleCategory mc) {
        super(name);
        this.category = mc;
    }

    @Override
    public void display(int mouseX, int mouseY, float partialTicks, int mouseDWheel) {
        super.display(mouseX, mouseY, partialTicks, mouseDWheel);
        scroll = AnimationUtils.smoothAnimation(scroll, scroll_temp, 50f, 0.3f);
        double my = this.y + this.TITLE_HEIGHT + 10 + scroll;
        int mn = 0;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() != this.category) {
                continue;
            }
            RenderUtils.drawImage(m.getState() ? new ResourceLocation("theresa/icons/enabled.png") : new ResourceLocation("theresa/icons/disabled.png"), x + 5, my - 1, 8, 8);
            Main.INSTANCE.fontLoaders.get("roboto", 18).drawString(m.getName(), (float) (this.x + 20), (float) my, m.getState() ? new Color(68, 119, 255).getRGB() : new Color(147, 147, 147).getRGB());
            my += 25;
            mn++;
        }
        if (drag1 && (my - dragY1 - y <= my)) {
            this.height = mouseY - dragY1 - y;
        }
        if (height > mn * 25 + TITLE_HEIGHT + 10) {
            height = mn * 25 + TITLE_HEIGHT + 10;
        }
        // Draw the slider bar
        double slider_height = ((height - TITLE_HEIGHT - 10) / (mn * 25)) * (height - TITLE_HEIGHT - 10);

        RenderUtils.drawRect(x + width - 5, y + TITLE_HEIGHT + (Math.abs(scroll) / (mn * 25)) * (height - TITLE_HEIGHT - 20), x + width - 4, y + TITLE_HEIGHT + (Math.abs(scroll) / (mn * 25)) * (height - TITLE_HEIGHT - 20) + slider_height, new Color(208, 208, 208).getRGB());
        if (isHovered(x, x + width, y + TITLE_HEIGHT, y + height - 10, mouseX, mouseY)) {
            if (mouseDWheel > 0 && scroll_temp <= 0) {
                scroll_temp += 8;
                if (scroll_temp > 0) scroll_temp = 0;
            } else if (mouseDWheel < 0 && my > y + height + 10) {
                scroll_temp -= 8;
            }
        }
    }

    @Override
    public void onClicked(double mx, double my, int mouseButton) {
        if (!isHovered(x, x + width, y, y + height - 10, mx, my)) {
            return;
        }
        double my1 = this.y + this.TITLE_HEIGHT + 10 + scroll;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() != this.category) {
                continue;
            }
            if (isHovered(x, x + width, my1 - 5, my1 + 15, mx, my) && mouseButton == 0) {
                m.setState(!m.getState());
            }
            my1 += 25;
        }

    }
}
