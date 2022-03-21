package cn.loli.client.script;

import cn.loli.client.Main;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.script.java.sfontmanager.SFontLoader;
import cn.loli.client.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.jetbrains.annotations.NotNull;

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

}
