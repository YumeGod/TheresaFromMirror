

package cn.loli.client.module.modules.misc;

import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.Main;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.ScaledResolution;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HUD extends Module {
    private final BooleanValue showClientInfo = new BooleanValue("ClientInfo", true);
    private final BooleanValue showArrayList = new BooleanValue("ArrayList", true);
    private final BooleanValue showNotifications = new BooleanValue("Notifications", true);

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


    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        if (!getState()) return;

        ScaledResolution res = new ScaledResolution(mc);


        if (showNotifications.getObject()) {
            NotificationManager.render();
        }
    }
}
