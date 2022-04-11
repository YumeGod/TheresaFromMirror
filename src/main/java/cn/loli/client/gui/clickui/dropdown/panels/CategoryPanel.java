package cn.loli.client.gui.clickui.dropdown.panels;

import cn.loli.client.Main;
import cn.loli.client.gui.clickui.dropdown.ClickUI;
import cn.loli.client.gui.clickui.dropdown.Panel;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.AnimationUtils;
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
    public void display(int mouseX, int mouseY, float partialTicks, int mouseDWheel) {
        super.display(mouseX, mouseY, partialTicks, mouseDWheel);
        double my = this.y + this.TITLE_HEIGHT + 10 + scroll;
        int mn = 0;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() != this.category) {
                continue;// 如果模块不属于当前分类，则跳过
            }
            // 模块状态
            RenderUtils.drawImage(m.getState() ? new ResourceLocation("theresa/icons/enabled.png") : new ResourceLocation("theresa/icons/disabled.png"), x + 5, my - 1, 8, 8);
            Main.INSTANCE.fontLoaders.get("roboto", 18).drawString(m.getName(), (float) (this.x + 20), (float) my, m.getState() ? new Color(68, 119, 255).getRGB() : new Color(147, 147, 147).getRGB());
            my += 25;
            mn++;
        }

        maxHeight = mn * 25 + TITLE_HEIGHT + 10;
    }

    @Override
    public void onClicked(double mx, double my, int mouseButton) {

        if (!isHovered(x, x + width, y, y + height - 10, mx, my)) {
            return; // 如果不在范围内，则不处理
        }

        // 处理鼠标点击事件
        double my1 = this.y + this.TITLE_HEIGHT + 10 + scroll;
        for (Module m : Main.INSTANCE.moduleManager.getModules()) {
            if (m.getCategory() != this.category) {
                continue;// 如果模块不属于当前分类，则跳过
            }
            if (isHovered(x, x + width, my1 - 5, my1 + 15, mx, my)) {
                if (mouseButton == 0) {
                    m.setState(!m.getState());// 如果鼠标左键点击，则切换模块状态
                } else if (mouseButton == 1 && Main.INSTANCE.valueManager.getAllValuesFrom(m.getName()).size() > 0) {
                    ClickUI.panels_temp.add(new ValuePanel(m));// 如果鼠标右键点击，则打开模块值面板
                }
            }
            my1 += 25;
        }

    }
}
