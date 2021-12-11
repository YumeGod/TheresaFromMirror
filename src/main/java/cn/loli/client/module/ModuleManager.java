

package cn.loli.client.module;

import cn.loli.client.module.modules.combat.*;
import cn.loli.client.module.modules.misc.ClickGUIModule;
import cn.loli.client.module.modules.misc.HUD;
import cn.loli.client.module.modules.movement.*;
import cn.loli.client.module.modules.player.AutoTools;
import cn.loli.client.module.modules.render.*;
import cn.loli.client.module.modules.player.NoFall;
import cn.loli.client.module.modules.player.NoRightClickDelay;
import cn.loli.client.module.modules.player.SpeedMine;
import cn.loli.client.module.modules.world.Eagle;
import cn.loli.client.module.modules.world.Timer;
import cn.loli.client.scripting.ScriptModule;
import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class ModuleManager {
    @NotNull
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    public void addModules() {
        //Combat
        addModule(new AimBot());
     //   addModule(new Aura());
        addModule(new AutoClicker());
        addModule(new Criticals());
        addModule(new KeepSprint());
        addModule(new Velocity());

        //Movement
        addModule(new FlagDetector());
        addModule(new Fly());
        addModule(new NoJumpDelay());
        addModule(new NoSlowDown());
        addModule(new Sprint());

        //Player
        addModule(new AutoTools());
        addModule(new NoFall());
        addModule(new NoRightClickDelay());
        addModule(new SpeedMine());


        //Render
        addModule(new BlockHit());
        addModule(new FullBright());
        addModule(new ItemRenderer());
        addModule(new OldAnimations());
        addModule(new Tracers());
        addModule(new ViewClip());

        //World
        addModule(new Eagle());
        addModule(new Timer());


        //Other
        addModule(new HUD()); // Needs to be second last
        addModule(new ClickGUIModule()); // Needs to be last
    }

    private void addModule(@NotNull Module module) {
        modules.add(module);
        Main.INSTANCE.valueManager.registerObject(module.getName(), module);
    }

    @NotNull
    public List<Module> getModules() {
        return modules;
    }

    public TreeSet<Module> getModulesSorted() {
        TreeSet<Module> moduleTreeSet = new TreeSet<>(Comparator.comparing(Module::getName));
        moduleTreeSet.addAll(modules);
        return moduleTreeSet;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.stream().filter(mod -> mod.getClass() == clazz).findFirst().orElse(null);
    }

    public Module getModule(@NotNull String name, boolean caseSensitive) {
        return modules.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    @EventTarget
    private void onKey(@NotNull KeyEvent event) {
        for (Module module : modules) if (module.getKeybind() == event.getKey()) module.setState(!module.getState());
    }

    public void addScriptModule(ScriptModule module) {
        addModule(module);
    }
}
