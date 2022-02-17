

package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.AnimationUtils;
import cn.loli.client.utils.MoveUtils;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HUD extends Module {
    private final BooleanValue showClientInfo = new BooleanValue("ClientInfo", true);
    private final BooleanValue showArrayList = new BooleanValue("ArrayList", true);
    private final BooleanValue showNotifications = new BooleanValue("Notifications", true);
    private NumberValue<Number> ArrayListXPos = new NumberValue<>("ArrayListXPos", 0, 0, 15);
    private NumberValue<Number> ArrayListYPos = new NumberValue<>("ArrayListYPos", 0, 0, 15);
    public static ModeValue mode = new ModeValue("Mode", "Normal", "Normal");

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    List<Module> sort;
    private boolean sorted = false;

    public HUD() {
        super("HUD", "The heads up display overlay", ModuleCategory.MISC);
        setState(true);
        sort = Main.INSTANCE.moduleManager.getModules();
    }

    //反转ArrayList
    private static ArrayList<Module> reverse(List<Module> list) {
        ArrayList<Module> newList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            newList.add(list.get(i));
        }
        return newList;
    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        if (!getState()) return;
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        HFontRenderer font = Main.fontLoaders.fonts.get("roboto16");
        if (!sorted) {
            sort.sort(Comparator.comparingInt(m -> font.getStringWidth(m.getName())));
            sort = reverse(sort);
            sorted = true;
        }

        int i = ArrayListYPos.getObject().intValue();


        double currSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);

        if (showClientInfo.getObject()) {
            int fpsWidth = mc.fontRendererObj.drawString("FPS: " + Minecraft.getDebugFPS(), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true);
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("BPS: %.2f", currSpeed), 2, res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT * 2 - 2, -1, true));
            fpsWidth = Math.max(fpsWidth, mc.fontRendererObj.drawString(String.format("User: " + Main.INSTANCE.name, currSpeed), (float) (res.getScaledWidth() - mc.fontRendererObj.getStringWidth(String.format("User: " + Main.INSTANCE.name, currSpeed))), res.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT - 2, -1, true));
        }

        if (showArrayList.getObject()) {
            for (Module m : sort) {
                if (m.getState()) {
                    String s = m.getName();
                    font.drawString(s, res.getScaledWidth() - m.arraylist_animX, m.arraylist_animY, -1);
                    m.arraylist_animY = AnimationUtils.smoothAnimation(m.arraylist_animY, i + ArrayListYPos.getObject().intValue(), 50, .3f);
                    m.arraylist_animX = AnimationUtils.smoothAnimation(m.arraylist_animX, font.getStringWidth(s) + ArrayListXPos.getObject().intValue(), 50, .4f);
                    i += 16;
                }
            }
        }

        if (showNotifications.getObject()) {
            NotificationManager.render();
        }
    }
}
