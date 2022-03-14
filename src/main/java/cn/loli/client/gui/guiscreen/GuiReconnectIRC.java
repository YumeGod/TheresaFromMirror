package cn.loli.client.gui.guiscreen;

import cn.loli.client.Main;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class GuiReconnectIRC extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
//        Main.INSTANCE.bootstrap.connect();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        RenderUtils.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -1);
        Main.INSTANCE.fontLoaders.fonts.get("roboto23").drawString("Choose your proxy.", scaledResolution.getScaledWidth() / 2 - Main.INSTANCE.fontLoaders.fonts.get("roboto23").getStringWidth("Choose your proxy.") / 2f, 20, new Color(0, 0, 0).getRGB());
        int x1 = 10;
        int y1 = 60;

        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    RenderUtils.drawImage(new ResourceLocation("theresa/icons/JP.png"), x1, y1, 64, 64);
                    Main.INSTANCE.fontLoaders.fonts.get("roboto20").drawString("JP-1", x1 + 32 - Main.INSTANCE.fontLoaders.fonts.get("roboto20").getStringWidth("JP-1") / 2f, y1 + 70, new Color(0, 0, 0).getRGB());
                    break;
                case 1:
                    RenderUtils.drawImage(new ResourceLocation("theresa/icons/cn.png"), x1, y1, 64, 64);
                    Main.INSTANCE.fontLoaders.fonts.get("roboto20").drawString("CN-1", x1 + 32 - Main.INSTANCE.fontLoaders.fonts.get("roboto20").getStringWidth("CN-1") / 2f, y1 + 70, new Color(0, 0, 0).getRGB());
                    break;
                case 2:
                    RenderUtils.drawImage(new ResourceLocation("theresa/icons/US.png"), x1, y1, 64, 64);
                    Main.INSTANCE.fontLoaders.fonts.get("roboto20").drawString("US-1", x1 + 32 - Main.INSTANCE.fontLoaders.fonts.get("roboto20").getStringWidth("US-1") / 2f, y1 + 70, new Color(0, 0, 0).getRGB());
                    break;
                case 3:
                    RenderUtils.drawImage(new ResourceLocation("theresa/icons/US.png"), x1, y1, 64, 64);
                    Main.INSTANCE.fontLoaders.fonts.get("roboto20").drawString("US-2", x1 + 32 - Main.INSTANCE.fontLoaders.fonts.get("roboto20").getStringWidth("US-2") / 2f, y1 + 70, new Color(0, 0, 0).getRGB());
                    break;
            }

            if (isHovered(x1, y1, x1 + 64, y1 + 80, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                switch (i) {
                    case 0:
                        Main.INSTANCE.bootstrap.connect(getip("Japan-1"), 9822);
                        break;
                    case 1:
                        Main.INSTANCE.bootstrap.connect(getip("HK-2"), 9822);
                        break;
                    case 2:
                        Main.INSTANCE.bootstrap.connect(getip("US-1"), 9822);
                        break;
                    case 3:
                        Main.INSTANCE.bootstrap.connect(getip("US-2"), 9822);
                        break;
                }
                mc.displayGuiScreen(new GuiMainMenu());
            }
            x1 += 100;
            if (x1 > scaledResolution.getScaledWidth() - 68) {
                x1 = 10;
                y1 += 80;
            }
        }
    }


    public String getip(String name) {
        switch (name) {
            case "Japan-1":
                return "167.88.184.79";
            case "HK-2":
                return "45.94.41.7";
            case "US-1":
                return "15.204.152.11";
            case "US-2":
                return "15.204.152.34";
        }
        return "15.204.152.34";
    }

    public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
//        Main.INSTANCE.doCrash();
    }
}