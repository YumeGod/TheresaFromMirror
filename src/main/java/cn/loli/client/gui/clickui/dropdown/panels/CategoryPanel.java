package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class CategoryPanel extends Panel {
    private ModuleCategory category;

    public CategoryPanel(String name, ModuleCategory mc) {
        super(name);
        this.category = mc;
    }

    @Override
    public void display(int mouseX, int mouseY, float partialTicks) {
        super.display(mouseX, mouseY, partialTicks);
        HFontRenderer titleFont = Main.INSTANCE.fontLoaders.fonts.get("roboto22");
        RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + name.toLowerCase() + ".png"), (float) (x + width / 2) - titleFont.getStringWidth(name) / 2f - 8, y + 10, 8, 8, new Color(61, 61, 61));
        double my = this.y + this.TITLE_HEIGHT + 10;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() != this.category) {
                continue;
            }
            RenderUtils.drawImage(m.getState() ? new ResourceLocation("theresa/icons/enabled.png") : new ResourceLocation("theresa/icons/disabled.png"), x + 5, my - 1, 8, 8);
            Main.INSTANCE.fontLoaders.get("roboto", 18).drawString(m.getName(), (float) (this.x + 20), (float) my, m.getState() ? new Color(68, 119, 255).getRGB() : new Color(147, 147, 147).getRGB());
            my += 25;
        }
    }

    @Override
    public void onClicked(double mx, double my, int mouseButton) {
        if (!isHovered(x, x + width, y, y + height - 10, mx, my)) {
            return;
        }
        double my1 = this.y + this.TITLE_HEIGHT + 10;
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
