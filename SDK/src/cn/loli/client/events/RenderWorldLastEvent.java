

package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.renderer.RenderGlobal;

public class RenderWorldLastEvent implements Event {
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
