package cn.loli.client.gui.guiscreen;

import cn.loli.client.Main;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiReconnectIRC extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
//        Main.INSTANCE.bootstrap.connect();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        RenderUtils.drawRect(0,0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -1);
    }
    public String getip(String name){
        switch (name){
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
