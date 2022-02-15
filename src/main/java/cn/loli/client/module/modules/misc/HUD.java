

package cn.loli.client.module.modules.misc;

import cn.loli.client.Main;
import cn.loli.client.gui.ttfr.HFontRenderer;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
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
    List<Module> sort = new ArrayList<>();

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
        ScaledResolution res = new ScaledResolution(mc);
        HFontRenderer font = Main.fontLoaders.fonts.get("roboto16");
        int i = ArrayListYPos.getObject();
        sort.sort(Comparator.comparingInt(m -> m.getName().length()));
        sort = reverse(sort);
        for (Module m : sort) {
            if (m.getState()) {
                String s = m.getName();
                font.drawString(s, res.getScaledWidth() - m.arraylist_animX, m.arraylist_animY, -1);
                m.arraylist_animY = m.arraylist_animY_timer.animate(i, m.arraylist_animY +  ArrayListYPos.getObject().intValue(), 0.2f, 20);
                m.arraylist_animX = m.arraylist_animX_timer.animate(font.getStringWidth(s) + ArrayListXPos.getObject().intValue(), m.arraylist_animX, 0.2f, 20);
                i += 16;
            }
        }

        if (showNotifications.getObject()) {
            NotificationManager.render();
        }
    }
}
