package dev.xix.feature.module;

import cn.loli.client.module.modules.combat.Velocity;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import dev.xix.TheresaClient;
import dev.xix.feature.module.combat.VelocityModule;

import java.util.Arrays;

public final class TheresaModuleManager {
    private final ImmutableMap<Class<? extends AbstractTheresaModule>, AbstractTheresaModule> modules;


    public TheresaModuleManager() {
        this.modules = registerModules(
                // COMBAT
                new VelocityModule()
                // MOVEMENT
                // PLAYER
                // WORLD
                // EXPLOIT
                // MISCELLANEOUS
                // RENDER
        );
    }

    public <T extends AbstractTheresaModule> T getModuleOrNull(final Class<T> clazz) {
        for (final AbstractTheresaModule module : modules.values()) {
            if (module.getClass().isInstance(clazz)) {
                return (T) module;
            }
        }
        return null;
    }

    public static <T extends AbstractTheresaModule> T getInstanceOrNull(final Class<T> clazz) {
        return TheresaClient.getInstance().getModuleManager().getModuleOrNull(clazz);
    }

    private ImmutableMap<Class<? extends AbstractTheresaModule>, AbstractTheresaModule> registerModules(final AbstractTheresaModule... modules) {
        final ImmutableMap.Builder<Class<? extends AbstractTheresaModule>, AbstractTheresaModule> moduleBuilder = ImmutableMap.builder();
        Arrays.stream(modules).forEach(o -> moduleBuilder.put(o.getClass(), o));
        return moduleBuilder.build();
    }

    public ImmutableMap<Class<? extends AbstractTheresaModule>, AbstractTheresaModule> getModules() {
        return modules;
    }
}
