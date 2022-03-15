package cn.loli.client.gui.guiscreen;

import cn.loli.client.Main;
import cn.loli.client.connection.ProxyEntry;
import cn.loli.client.connection.RSAUtils;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class GuiReconnectIRC extends GuiScreen {

    ArrayList<ProxyEntry> proxys = new ArrayList<>();
    GuiScreen parent;
    boolean isSelected = false;

    public GuiReconnectIRC(GuiScreen perscreen) {
        parent = perscreen;
        isSelected = false;
    }

    @Override
    public void initGui() {
        super.initGui();
        proxys.add(new ProxyEntry("JP", "167.88.184.79"));
        proxys.add(new ProxyEntry("CN", "2404:8c80:0:1009:395:fbec:c4f8:e384"));
        proxys.add(new ProxyEntry("US", "15.204.152.34", "15.204.152.11"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtils.drawRoundedRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 2, new Color(240, 240, 240).getRGB());
        Main.INSTANCE.fontLoaders.fonts.get("roboto23").drawString("Your Connection has been lost.Please reconnect!", scaledResolution.getScaledWidth() / 2f - Main.INSTANCE.fontLoaders.fonts.get("roboto23").getStringWidth("Your Connection has been lost.Please reconnect!") / 2f, 30, new Color(79, 129, 255).getRGB());
        int width = scaledResolution.getScaledWidth() / 4;
        int x1 = width / 4;
        for (ProxyEntry proxy : proxys) {
            String name = proxy.name;
            RenderUtils.drawRect(x1, 50, x1 + width, scaledResolution.getScaledHeight() - 20, new Color(255, 255, 255).getRGB());
            int by = 0;
            for (String ip : proxy.proxyList) {
                int y = scaledResolution.getScaledHeight() - 70 - by * 30;
                RenderUtils.drawRoundedRect(x1 + 15, y, width - 30, 20, 2, new Color(79, 129, 255).getRGB());
                Main.INSTANCE.fontLoaders.fonts.get("roboto18").drawString(name + "-" + (by + 1), x1 + width / 2f - Main.INSTANCE.fontLoaders.fonts.get("roboto18").getStringWidth(name) / 2f, y + 5, new Color(255, 255, 255).getRGB());
                if (isHovered(x1 + 15, y, x1 + width - 30, y + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    if (isSelected) return;
                    //Init
                    Main.INSTANCE.hasKey = false;
                    //Re Generate RSA
                    Map<String, String> keyMap = RSAUtils.createKeys(2048);
                    Main.INSTANCE.publicKey = keyMap.get("publicKey");
                    Main.INSTANCE.privateKey = keyMap.get("privateKey");

                    new Thread(() -> {
                        Main.INSTANCE.cf = Main.INSTANCE.bootstrap.connect(ip, 9822);
                        Main.INSTANCE.println("Reconnected");
                    }).start();

                    Minecraft.getMinecraft().displayGuiScreen(parent);
                    isSelected = true;
                }
                by++;
            }
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + name + ".png"), x1 + width / 2 - 32, 100, 64, 64);
            x1 += width + width / 4;
        }
    }

    //TODO : USE DNS TO SOLVE THE IP SOURCE
    public String getIP(String name) {
        return "";
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
