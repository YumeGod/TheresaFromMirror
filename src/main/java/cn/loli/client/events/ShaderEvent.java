package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.shader.ShaderGroup;

public class ShaderEvent implements Event {
    public ShaderGroup shader;
    public boolean useShader;

    public ShaderEvent(ShaderGroup shader, boolean useShader) {
        this.shader = shader;
        this.useShader = useShader;
    }

    public void setShader(ShaderGroup shader) {
        this.shader = shader;
    }

    public void setUseShader(boolean useShader) {
        this.useShader = useShader;
    }

    public ShaderGroup getShader() {
        return shader;
    }

    public boolean isUseShader() {
        return useShader;
    }
}
