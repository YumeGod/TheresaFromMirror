package cn.loli.client.gui.clickui.dropdown;

import cn.loli.client.gui.clickui.dropdown.panels.CategoryPanel;
import cn.loli.client.module.ModuleCategory;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ClickUI extends GuiScreen {
    public ArrayList<Panel> panels = new ArrayList<>();

    @Override
    public void initGui() {
        super.initGui();

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

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int mouseDWheel = Mouse.getDWheel();
        for (Panel panel : panels) {
            panel.draw(mouseX, mouseY, partialTicks,mouseDWheel);
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
