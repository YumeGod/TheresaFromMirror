package cn.loli.client.events;

import dev.xix.event.Event;
import net.minecraft.client.renderer.RenderGlobal;

public class RenderWorldLastEvent extends Event {
    private final RenderGlobal renderGlobal;
    private final float partialTicks;

    public RenderWorldLastEvent(RenderGlobal renderGlobal, float partialTicks) {
        this.renderGlobal = renderGlobal;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getRenderGlobal() {
        return renderGlobal;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
