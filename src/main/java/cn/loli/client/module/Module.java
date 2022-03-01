

package cn.loli.client.module;

import cn.loli.client.Main;
import cn.loli.client.module.modules.misc.HUD;
import cn.loli.client.notifications.Notification;
import cn.loli.client.notifications.NotificationManager;
import cn.loli.client.notifications.NotificationType;
import cn.loli.client.utils.player.InventoryUtil;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.utils.player.movement.MoveUtils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final GameSettings game = mc.gameSettings;
    protected static final Random r = new Random();
    protected final static PlayerUtils playerUtils = PlayerUtils.getInstance();
    protected final static RotationUtils rotationUtils = RotationUtils.getInstance();
    protected final static InventoryUtil inventoryUtil = InventoryUtil.getInstance();
    protected final static MoveUtils moveUtils = MoveUtils.getInstance();

    private final String name;
    private final String description;
    private final ModuleCategory category;
    private final boolean canBeEnabled;
    private final boolean hidden;
    private int keybind;
    protected boolean state;

    boolean keepReg = false;
    boolean isReg = false;

    //Animations
    public float clickgui_animY;
    public float clickgui_animX;
    public float arraylist_animX;
    public float arraylist_animY;




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
    }

    protected void onDisable() {
    }

    protected void onToggle() {
    }
}
