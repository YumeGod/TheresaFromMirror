package cn.loli.client.gui.clickui.dropdown;

import cn.loli.client.gui.clickui.dropdown.panels.CategoryPanel;
import cn.loli.client.module.ModuleCategory;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ClickUI extends GuiScreen {
    public static ArrayList<Panel> panels = new ArrayList<>();
    public static ArrayList<Panel> panels_add_temp = new ArrayList<>();
    public static ArrayList<Panel> panels_remove_temp = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();

        if (panels.size() == 0) {
            panels.clear();
            for (ModuleCategory category : ModuleCategory.values()) {
                panels.add(new CategoryPanel(category.name(), category));
            }

            int x = 0;
            for (Panel panel : panels) {
                panel.update(10 + x, 60, 110, 200);
                x += 120;
            }
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (panels_add_temp.size() != 0) {
            panels.addAll(panels_add_temp);
            panels_add_temp.clear();
        }
        if (panels_remove_temp.size() != 0) {
            panels.removeAll(panels_remove_temp);
            panels_remove_temp.clear();
        }
        int mouseDWheel = Mouse.getDWheel();
        for (Panel panel : panels) {
            panel.draw(mouseX, mouseY, partialTicks, mouseDWheel);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (Panel panel : panels) {
            panel.onClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
}
