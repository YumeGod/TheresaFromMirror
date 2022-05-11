package cn.loli.client.module.modules.render;

import cn.loli.client.events.CameraEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.event.bus.IEventListener;
import org.lwjgl.opengl.Display;

public class CamaraDebug extends Module {

    private static float cameraYaw = 0F;
    private static float cameraPitch = 0F;

    public CamaraDebug() {
        super("CameraDebug", "Camara Debug", ModuleCategory.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        cameraYaw = mc.thePlayer.rotationYaw;
        cameraPitch = mc.thePlayer.rotationPitch;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private final IEventListener<CameraEvent> onCamara = event ->
    {
        event.setYaw(cameraYaw);
        event.setPitch(cameraPitch);
        event.setPrevYaw(cameraYaw);
        event.setPrevPitch(cameraPitch);
    };


}
