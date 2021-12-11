

package cn.loli.client.module;

import cn.loli.client.module.modules.misc.HUD;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.Main;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final boolean canBeEnabled;
    private final boolean hidden;
    private int keybind;
    protected boolean state;

    protected Module(String name, String description, ModuleCategory moduleCategory) {
        this(name, description, moduleCategory, true, false, Keyboard.KEY_NONE);
    }

    protected Module(String name, String description, ModuleCategory category, boolean canBeEnabled, boolean hidden, int keybind) {
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

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        if (state) {
            this.state = true;
            onEnable();

            if (mc.thePlayer != null && Main.INSTANCE.moduleManager.getModule(HUD.class).getState()) {
                NotificationManager.show(new Notification(NotificationType.INFO, "Info", getName() + " was enabled", 1));
            }
        } else {
            this.state = false;
            onDisable();

            if (mc.thePlayer != null && Main.INSTANCE.moduleManager.getModule(HUD.class).getState()) {
                NotificationManager.show(new Notification(NotificationType.INFO, "Info", getName() + " was disabled", 1));
            }
        }

        onToggle();
    }

    protected void onEnable() {
        EventManager.register(this);
    }

    protected void onDisable() {
        EventManager.unregister(this);
    }

    protected void onToggle() { }
}
