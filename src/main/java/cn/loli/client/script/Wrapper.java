package cn.loli.client.script;

import cn.loli.client.Main;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.modules.combat.TargetStrafe;
import cn.loli.client.script.java.sfontmanager.SFontLoader;
import cn.loli.client.utils.player.movement.MoveUtils;
import cn.loli.client.utils.player.rotation.RotationHook;
import cn.loli.client.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wrapper {

    private static Wrapper wrapper;

    public static Wrapper getInstance() {
        if (wrapper == null) {
            wrapper = new Wrapper();
        }
        return wrapper;
    }

    //GOTO : ValueManager.java

    /**
     * @param name   The name of the owner
     * @param object The Value-object
     *               this method will add the value to the owner if it doesn't exist
     */
    public void register(String name, @NotNull Value object) {
        List<Value> values = new ArrayList<>();
        if (Main.INSTANCE.valueManager.valueMap.get(name) != null)
            values = Main.INSTANCE.valueManager.valueMap.get(name);

        values.add(object);
        Main.INSTANCE.valueManager.valueMap.put(name, values);
    }

    //GOTO : ModuleManager.java

    /**
     * this method will displayed all the modules
     */
    public List<String> getModulesList() {
        List<String> modules = new ArrayList<>();
        for (Module module : Main.INSTANCE.moduleManager.getModules())
            modules.add(module.getName());
        return modules;
    }

    /**
     * this method will get the module if enabled
     */
    public boolean getModuleStatus(@NotNull String name, boolean caseSensitive) {
        return Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).getState();
    }

    /**
     * this method will get the module's keybind
     */
    public int getModuleKeyBind(@NotNull String name, boolean caseSensitive) {
        return Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).getKeybind();
    }

    /**
     * this method will get the modules enabled list
     */
    public List<String> getModulesEnabledList() {
        List<String> modules = new ArrayList<>();
        for (Module module : Main.INSTANCE.moduleManager.getModules())
            if (module.getState())
                modules.add(module.getName());

        return modules;
    }

    /**
     * this method will get the shadowfontloader status
     */
    public SFontLoader getSFontLoader() {
        return Main.INSTANCE.sFontLoader;
    }


    /**
     * this method will get the timer  status
     */
    public Timer getTimer() {
        return ((IAccessorMinecraft) Minecraft.getMinecraft()).getTimer();
    }


    /**
     * this method will get the Current Yaw / Pitch
     */

    public float getCurrentYaw() {
        return RotationHook.yaw;
    }

    public float getCurrentPitch() {
        return RotationHook.pitch;
    }

    /*
     * this method will get the Current animation
     */

    public float getArraylistAnimationX(@NotNull String name, boolean caseSensitive) {
        return Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).animX;
    }

    public float getArraylistAnimationY(@NotNull String name, boolean caseSensitive) {
        return Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).animY;
    }

    public void setArraylistAnimationX(@NotNull String name, boolean caseSensitive, float value) {
        Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).animX = value;
    }

    public void setArraylistAnimationY(@NotNull String name, boolean caseSensitive, float value) {
        Objects.requireNonNull(Main.INSTANCE.moduleManager.getModules()
                .stream().filter(mod -> !caseSensitive &&
                        name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null)).animY = value;
    }

    //Set Speed with TargetStrafe Enable
    public void setMotion(PlayerMoveEvent event, float speed) {
        MoveUtils.getInstance().setSpeed(Minecraft.getMinecraft().thePlayer, event, Main.INSTANCE.moduleManager.getModule(TargetStrafe.class), speed);
    }

    //get value's bind
    public String getValueBind(String owner, String name, boolean ignoreSpace) {
        return Keyboard.getKeyName(Main.INSTANCE.valueManager.keyBind.get(Main.INSTANCE.valueManager.get(owner, name, ignoreSpace)));
    }

    //get all bind's value
    public ArrayList<String> getAllValueBinds() {
        ArrayList<String> arrayList = new ArrayList<>();
        Main.INSTANCE.valueManager.keyBind.keySet().forEach(key -> {
            arrayList.add(Main.INSTANCE.valueManager.ownerMap.get(key) + ":" + key.getName() + ":" + Keyboard.getKeyName(Main.INSTANCE.valueManager.keyBind.get(key)));
        });
        return arrayList;
    }
}
