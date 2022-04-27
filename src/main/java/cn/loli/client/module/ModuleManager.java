

package cn.loli.client.module;

import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.module.modules.combat.*;
import cn.loli.client.module.modules.misc.*;
import cn.loli.client.module.modules.misc.skyblock.AutoFarm;
import cn.loli.client.module.modules.movement.*;
import cn.loli.client.module.modules.player.*;
import cn.loli.client.module.modules.render.*;
import cn.loli.client.module.modules.world.Eagle;
import cn.loli.client.module.modules.world.Timer;
import cn.loli.client.utils.render.Tabgui;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModuleManager {
    @NotNull
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    public void addModules() {
        //Combat
        addModule(new AimBot());
        addModule(new Aura());
        addModule(new AutoClicker());
        addModule(new AutoPot());
        addModule(new BackTrack());
        addModule(new BowAimbot());
        addModule(new Criticals());
        addModule(new KeepSprint());
        addModule(new TargetStrafe());
        addModule(new Range());
        addModule(new Velocity());

        //Movement
        addModule(new AntiFall());
        addModule(new BaffleSpeed());
        addModule(new Speed());
        addModule(new Sneak());
        addModule(new FlagDetector());
        addModule(new Fly());
        addModule(new Booster());
        addModule(new NoJumpDelay());
        addModule(new NoSlowDown());
        addModule(new Sprint());
        addModule(new Step());

        //Player
        addModule(new AutoArmor());
        addModule(new AutoPlace());
        addModule(new AutoTools());
        addModule(new ChestStealer());
        addModule(new DamnBridge());
        addModule(new InvCleaner());
        addModule(new InventoryWalk());
        addModule(new LongJump());
        addModule(new MurderMystery());
        addModule(new NoFall());
        addModule(new NoRightClickDelay());
        addModule(new NoRotate());
        addModule(new SafeWalk());
        addModule(new Scaffold());
        addModule(new SpeedMine());
        addModule(new TellyBridge());
        addModule(new Phase());
        addModule(new RageBot());


        //Render
        addModule(new BlockHit());
      //  addModule(new CamaraDebug());
        addModule(new FullBright());
        addModule(new ItemESP());
        addModule(new ItemRenderer());
        addModule(new ESP());
        addModule(new NoFov());
        addModule(new OldAnimations());
        addModule(new TargetHUD());
        addModule(new Tracers());
        addModule(new Trajectories());
        addModule(new Trail());
        addModule(new ViewClip());
        addModule(new Nazi());
        addModule(new Scoreboard());
        addModule(new Tabgui());

        //World
        addModule(new Eagle());
        addModule(new Timer());


        //Other
        addModule(new Abuser());
        addModule(new AntiBot());
        addModule(new AntiVanish());
        addModule(new AlwaysRotate());
        addModule(new AutoFarm());
        addModule(new AutoFish());
        addModule(new AutoPlay());
        addModule(new Spoofer());
        addModule(new IgnoreCommands());
        addModule(new HUD()); // Needs to be second last
        addModule(new ClickGUIModule()); // Needs to be last

        // sort by alphabets
        modules.removeIf(Objects::isNull);
        modules.sort((mod, mod1) -> {
            int char0 = mod.getName().charAt(0);
            int char1 = mod1.getName().charAt(0);
            return -Integer.compare(char1, char0);
        });

    }

    public void addModule(@NotNull Module module) {
        modules.add(module);
        Main.INSTANCE.valueManager.registerObject(module.getName(), module);
    }

    public void addModuleNoReg(@NotNull Module module) {
        modules.add(module);
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

}
