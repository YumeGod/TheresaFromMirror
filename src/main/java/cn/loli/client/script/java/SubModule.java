package cn.loli.client.script.java;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.HUD;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class SubModule extends Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final GameSettings game = mc.gameSettings;
    protected static final Random r = new Random();

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final boolean canBeEnabled;
    private final boolean hidden;
    private int keybind;
    protected boolean state;

    boolean keepReg = false;
    boolean isReg = false;

    public SubModule(String name, String description, String moduleCategory) {
        this(name, description, ModuleCategory.getCategory(moduleCategory), true, false, Keyboard.KEY_NONE);
    }

    public SubModule(String name, String description, ModuleCategory moduleCategory) {
        this(name, description, moduleCategory, true, false, Keyboard.KEY_NONE);
    }

    public SubModule(String name, String description, ModuleCategory category, boolean canBeEnabled, boolean hidden, int keybind) {
        super(name, description, category, canBeEnabled, hidden, keybind);
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getKeybind() {
        return keybind;
    }

    @Override
    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean getState() {
        return state;
    }

    @Override
    public void setState(boolean state) {
        if (state) {
            this.state = true;

            if (mc.thePlayer != null)
                onEnable();

            arraylist_animX = 0;
            arraylist_animY -= 16;

            if (!isReg) {
                isReg = true;
                EventManager.register(this);
            }

            if (mc.thePlayer != null && Main.INSTANCE.moduleManager.getModule(HUD.class).getState()) {
                NotificationManager.show(new Notification(NotificationType.INFO, "Info", getName() + " was enabled", 1));
            }
        } else {
            this.state = false;

            if (mc.thePlayer != null)
                onDisable();

            if (!keepReg && isReg) {
                isReg = false;
                EventManager.unregister(this);
            }

            if (mc.thePlayer != null && Main.INSTANCE.moduleManager.getModule(HUD.class).getState()) {
                NotificationManager.show(new Notification(NotificationType.ERROR, "Info", getName() + " was disabled", 1));
            }

        }
    }


    protected void onEnable() {
        super.onEnable();
    }

    protected void onDisable() {
        super.onDisable();
    }

    protected void onToggle() {
        super.onToggle();
    }


}
