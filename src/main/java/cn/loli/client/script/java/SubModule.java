package cn.loli.client.script.java;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.HUD;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

public class SubModule extends Module {

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

            if(!Main.INSTANCE.moduleManager.getModule(HUD.class).arraylist_mods.contains(this)) {
                Main.INSTANCE.moduleManager.getModule(HUD.class).arraylist_mods.add(this);
                Main.INSTANCE.moduleManager.getModule(HUD.class).sort();
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                this.arraylist_animX = sr.getScaledWidth();
                if (arraylist_animY != 0) {
                    arraylist_animY -= 16;
                }
            }

            if (!isReg) {
                isReg = true;
                 Main.INSTANCE.eventBus.register(this);
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
                Main.INSTANCE.eventBus.unregister(this);
            }

            if (mc.thePlayer != null && Main.INSTANCE.moduleManager.getModule(HUD.class).getState()) {
                NotificationManager.show(new Notification(NotificationType.ERROR, "Info", getName() + " was disabled", 1));
            }

        }
    }


    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @Override
    protected void onToggle() {
        super.onToggle();
    }


}
