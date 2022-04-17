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
        //AvailableProxy
        proxys.add(new ProxyEntry("JP", "jp1.nigger.party", "jp2.nigger.party"));
        proxys.add(new ProxyEntry("HK", "hk1.nigger.party"));
        proxys.add(new ProxyEntry("US", "us1.nigger.party", "us2.nigger.party", "us3.nigger.party", "us4.nigger.party"));
        proxys.add(new ProxyEntry("Russia", "ru1.nigger.party", "ru2.nigger.party", "ru3.nigger.party", "ru4.nigger.party"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtils.drawRoundedRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 2, new Color(240, 240, 240).getRGB());
        Main.INSTANCE.fontLoaders.fonts.get("roboto23").drawString("Your Connection has been lost.Please reconnect!", scaledResolution.getScaledWidth() / 2f - Main.INSTANCE.fontLoaders.fonts.get("roboto23").getStringWidth("Your Connection has been lost.Please reconnect!") / 2f, 30, new Color(79, 129, 255).getRGB());
        int width = scaledResolution.getScaledWidth() / 5;
        int x1 = width / 5;
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
                    new Thread(() -> {
                        //Re Generate RSA
                        Main.INSTANCE.hasKey = false;
                        Map<String, String> keyMap = RSAUtils.createKeys(2048);
                        Main.INSTANCE.publicKey = keyMap.get("publicKey");
                        Main.INSTANCE.privateKey = keyMap.get("privateKey");
                        Main.INSTANCE.println(Main.INSTANCE.thread.isAlive() + " " + Main.INSTANCE.thread.isInterrupted());
                        Main.INSTANCE.thread.interrupt();
                        Main.INSTANCE.thread = new Thread(() -> {
                            Main.INSTANCE.ircLogin(ip);
                            Main.INSTANCE.println("Reconnected");
                        });
                        Main.INSTANCE.thread.start();
                    }).start();
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                    isSelected = true;
                }
                by++;
            }
            RenderUtils.drawImage(new ResourceLocation("theresa/icons/" + name.toLowerCase() + ".png"), x1 + width / 2f - 32, 100, 64, 64);
            x1 += width + width / 5;
        }
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
