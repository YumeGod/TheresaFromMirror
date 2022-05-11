package dev.xix.feature.module.combat;

import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.UpdateEvent;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.module.AbstractTheresaModule;
import dev.xix.feature.module.TheresaModuleCategory;
import dev.xix.feature.module.TheresaModuleManager;
import dev.xix.gui.animation.AnimationEasing;
import dev.xix.gui.animation.impl.MoveAnimation;
import dev.xix.gui.element.AbstractElement;
import dev.xix.gui.element.HorizontalAlignment;
import dev.xix.gui.element.VerticalAlignment;
import dev.xix.gui.element.impl.NodeElement;


public final class VelocityModule extends AbstractTheresaModule {
    public VelocityModule() {
        super("Velocity", TheresaModuleCategory.COMBAT);
        //int
    }

    private final IEventListener<Render2DEvent> updateEventIEventListener = event ->{
    };

    public static VelocityModule getInstance() {
        return TheresaModuleManager.getInstanceOrNull(VelocityModule.class);
    }
}
