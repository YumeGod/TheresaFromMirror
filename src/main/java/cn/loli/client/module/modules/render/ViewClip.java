package cn.loli.client.module.modules.render;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;

public class ViewClip extends Module {

    public final NumberValue<Integer> dis = new NumberValue<>("Distance", 10, 0, 50);
    public final BooleanValue extend = new BooleanValue("Extend-View", false);

    public ViewClip() {
        super("ViewClip", "Allows the camera to go through blocks", ModuleCategory.RENDER);
    }

}
