

package cn.loli.client.module.modules.misc;

import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.gui.tabgui.SubTab;
import cn.loli.client.gui.tabgui.Tab;
import cn.loli.client.gui.tabgui.TabGui;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.GLUtil;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HUD extends Module {
    private final BooleanValue showClientInfo = new BooleanValue("ClientInfo", true);
    private final BooleanValue hotbarOverlay = new BooleanValue("HotbarOverlay", true);
    private final BooleanValue showArrayList = new BooleanValue("ArrayList", true);
    private final BooleanValue showNotifications = new BooleanValue("Notifications", true);
    private final NumberValue<Integer> fpsStatisticLength = new NumberValue<>("FPSStatisticLength", 250, 10, 500);

    @NotNull
    private final TabGui<Module> tabGui = new TabGui<>();
    @NotNull
    private final List<Integer> fps = new ArrayList<>();

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public HUD() {
        super("HUD", "The heads up display overlay", ModuleCategory.MISC);
        setState(true);

        HashMap<ModuleCategory, List<Module>> moduleCategoryMap = new HashMap<>();

        for (Module module : Main.INSTANCE.moduleManager.getModules()) {
            if (!moduleCategoryMap.containsKey(module.getCategory())) {
                moduleCategoryMap.put(module.getCategory(), new ArrayList<>());
            }

            moduleCategoryMap.get(module.getCategory()).add(module);
        }

        moduleCategoryMap.entrySet().stream().sorted(Comparator.comparingInt(cat -> cat.getKey().toString().hashCode())).forEach(cat -> {
            Tab<Module> tab = new Tab<>(cat.getKey().toString());

            for (Module module : cat.getValue()) {
                tab.addSubTab(new SubTab<>(module.getName(), subTab -> subTab.getObject().setState(!subTab.getObject().getState()), module));
            }

            tabGui.addTab(tab);
        });

    }

    private static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        if (!getState()) return;

        FontRenderer fontRenderer = mc.fontRendererObj;
        ScaledResolution res = new ScaledResolution(mc);

        if (hotbarOverlay.getObject()) {
            fps.add(Minecraft.getDebugFPS());
            while (fps.size() > fpsStatisticLength.getObject()) {
                fps.remove(0);
            }

            int blackBarHeight = fontRenderer.FONT_HEIGHT * 2 + 4;
            Gui.drawRect(0, res.getScaledHeight() - blackBarHeight, res.getScaledWidth(), res.getScaledHeight(), GLUtil.toRGBA(new Color(0, 0, 0, 150)));

            GL11.glScaled(2.0, 2.0, 2.0);
            int initialSize = fontRenderer.drawString(Main.CLIENT_INITIALS, 1, res.getScaledHeight() / 2.0f - fontRenderer.FONT_HEIGHT, rainbow(0), true);
            GL11.glScaled(0.5, 0.5, 0.5);

            double currSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);

            int fpsWidth = fontRenderer.drawString("FPS: " + Minecraft.getDebugFPS(), initialSize * 2 + 2, res.getScaledHeight() - fontRenderer.FONT_HEIGHT - 2, -1, true);
            fpsWidth = Math.max(fpsWidth, fontRenderer.drawString(String.format("BPS: %.2f", currSpeed), initialSize * 2 + 2, res.getScaledHeight() - fontRenderer.FONT_HEIGHT * 2 - 2, -1, true));

            LocalDateTime now = LocalDateTime.now();
            String date = dateFormat.format(now);
            String time = timeFormat.format(now);

            fontRenderer.drawString(date, res.getScaledWidth() - fontRenderer.getStringWidth(date), res.getScaledHeight() - fontRenderer.FONT_HEIGHT - 2, -1, true);
            fontRenderer.drawString(time, res.getScaledWidth() - fontRenderer.getStringWidth(time), res.getScaledHeight() - fontRenderer.FONT_HEIGHT * 2 - 2, -1, true);

            int max = fps.stream().max(Integer::compareTo).orElse(1);
            double transform = blackBarHeight / 2.0 / (double) max;

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glLineWidth(1.0f);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            fpsWidth += 3;

            double v = ((res.getScaledWidth() / 2.0 - 100) - fpsWidth) / (double) fps.size();

            for (int j = 0; j < fps.size(); j++) {
                int currFPS = fps.get(j);

                GL11.glVertex2d(fpsWidth + j * v, res.getScaledHeight() - transform * currFPS);
            }

            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        if (showClientInfo.getObject()) {
            GL11.glScaled(2.0, 2.0, 2.0);
            int i = fontRenderer.drawString("溯夜观星", 2, 2, rainbow(0), true);
            GL11.glScaled(0.5, 0.5, 0.5);

            fontRenderer.drawString(Main.CLIENT_VERSION, i * 2, fontRenderer.FONT_HEIGHT * 2 - 7, rainbow(100), true);
            fontRenderer.drawString("by " + Main.CLIENT_AUTHOR, 4, fontRenderer.FONT_HEIGHT * 2 + 2, rainbow(200), true);
        }

        if (showArrayList.getObject()) {
            AtomicInteger offset = new AtomicInteger(3);
            AtomicInteger index = new AtomicInteger();

            Main.INSTANCE.moduleManager.getModules().stream().filter(mod -> mod.getState() && !mod.isHidden()).sorted(Comparator.comparingInt(mod -> -fontRenderer.getStringWidth(mod.getName()))).forEach(mod -> {
                fontRenderer.drawString(mod.getName(), res.getScaledWidth() - fontRenderer.getStringWidth(mod.getName()) - 3, offset.get(), rainbow(index.get() * 100), true);

                offset.addAndGet(fontRenderer.FONT_HEIGHT + 2);
                index.getAndIncrement();
            });
        }

        if (showNotifications.getObject()) {
            NotificationManager.render();
        }

    }

    @EventTarget
    public void onKey(@NotNull KeyEvent event) {

    }
}
