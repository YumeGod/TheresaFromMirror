

package cn.loli.client.gui.tabgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Tab<T> {
    @NotNull
    private final List<SubTab<T>> subTabs = new ArrayList<>();
    private String text;

    public Tab(String text) {
        this.text = text;
    }

    public void addSubTab(SubTab<T> subTab) {
        subTabs.add(subTab);
    }

    @NotNull
    public List<SubTab<T>> getSubTabs() {
        return subTabs;
    }

    public void renderSubTabs(int x, int y, int selectedSubTab) {

        glTranslated(x, y, 0);

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        int height = (font.FONT_HEIGHT + TabGui.OFFSET) * subTabs.size();

        int width = 0;

        for (SubTab<T> tab : subTabs) {
            if (font.getStringWidth(tab.getText()) > width) {
                width = font.getStringWidth(tab.getText());
            }
        }

        width += 2 + 2;

        TabGui.drawRect(GL_QUADS, 0, 0, width, height, TabGui.BACKGROUND.getRGB());

        glLineWidth(1.0f);
        TabGui.drawRect(GL_LINE_LOOP, 0, 0, width, height, TabGui.BORDER.getRGB());

        int offset = 2;

        int i = 0;

        for (SubTab<T> tab : subTabs) {
            if (selectedSubTab == i) {
                TabGui.drawRect(GL_QUADS, 0, offset - 2, width, offset + font.FONT_HEIGHT + TabGui.OFFSET - 1, TabGui.SELECTED.getRGB());
            }

            font.drawString(tab.getText(), 2, offset, TabGui.FOREGROUND.getRGB());
            offset += font.FONT_HEIGHT + TabGui.OFFSET;
            i++;
        }

        glTranslated(-x, -y, 0);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
