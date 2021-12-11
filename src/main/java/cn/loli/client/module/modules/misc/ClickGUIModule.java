

package cn.loli.client.module.modules.misc;

import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.WindowResizeEvent;
import cn.loli.client.gui.clickgui.ClickGUI;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

import static cn.loli.client.utils.Utils.getFileFromResourceAsStream;

public class ClickGUIModule extends Module {
    public ClickGUI clickGui;
    private int oldGuiScale;

    private final ModeValue font = new ModeValue("Font", "Roboto", Arrays.stream(Fonts.values()).map(Fonts::getName).toArray(String[]::new));

    public ClickGUIModule() {
        super("ClickGUI", "GUI that allows you to toggle modules and change settings", ModuleCategory.MISC, true, false, Keyboard.KEY_RSHIFT);

        font.setCallback((value) -> {
            if (mc.thePlayer != null)
                NotificationManager.show(new Notification(NotificationType.INFO, "Info", "ClickGUI font will be set after GUI is reset", 2));
        });
    }

    public void createClickGui() {
        try {
            clickGui = new ClickGUI(getFileFromResourceAsStream(Arrays.stream(Fonts.values()).filter(fonts -> fonts.getName()
                    .equalsIgnoreCase(font.getCurrentMode())).map(Fonts::getPath).findFirst().orElse(Fonts.ROBOTO.getPath())), 16,
                    Arrays.stream(Fonts.values()).filter(fonts -> fonts.getName().equalsIgnoreCase(font.getCurrentMode())).map(Fonts::getYOffset).findFirst().orElse(0f));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onEnable() {
        mc.displayGuiScreen(clickGui);
        setState(false);
        EventManager.register(this);
    }

    @Override
    public void setState(boolean state) {
        if (state) {
            this.state = true;
            onEnable();
        } else {
            this.state = false;
            onDisable();
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (event.getEventType() != EventType.POST) return;

        if (mc.gameSettings.guiScale != oldGuiScale) {
            onResize();
            oldGuiScale = mc.gameSettings.guiScale;
        }
    }

    @EventTarget
    public void onWindowResize(WindowResizeEvent event) {
        onResize();
    }

    private void onResize() {
        if (clickGui == null) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);

        clickGui.window.setWidth(sr.getScaleFactor() < 2 ? (mc.displayWidth - 50) : (mc.displayWidth - 50) / (sr.getScaleFactor() - 1));
        clickGui.window.setHeight(sr.getScaleFactor() < 2 ? (mc.displayHeight - 50) : (mc.displayHeight - 50) / (sr.getScaleFactor() - 1));
        clickGui.window.setX(mc.displayWidth / sr.getScaleFactor() - clickGui.window.getWidth() / 2);
        clickGui.window.setY(mc.displayHeight / sr.getScaleFactor() - clickGui.window.getHeight() / 2);
    }

    public void resetClickGui() {
        createClickGui();
        onResize();
        mc.displayGuiScreen(clickGui);
    }

    private enum Fonts {
        INTER("Inter", "theresa/font/inter.ttf", 0),
        NUNITO("Nunito", "theresa/font/nunito.ttf", -0.5f),
        OPENSANS("Open Sans", "theresa/font/opensans.ttf", -0.75f),
        ROBOTO("Roboto", "theresa/font/roboto.ttf", 0),
        UBUNTU("Ubuntu", "theresa/font/ubuntu.ttf", -0.25f);

        private final String name;
        private final String path;
        private final float yOffset;

        Fonts(String name, String path, float yOffset) {
            this.name = name;
            this.path = path;
            this.yOffset = yOffset;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public float getYOffset() {
            return yOffset;
        }
    }
}
