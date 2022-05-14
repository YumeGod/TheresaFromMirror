package cn.loli.client.module.modules.render;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;

public class ViewClip extends Module {

    public final NumberProperty<Integer> dis = new NumberProperty<>("Distance", 10, 0, 50 , 1);
    public final BooleanProperty extend = new BooleanProperty("Extend-View", false);

    public ViewClip() {
        super("ViewClip", "Allows the camera to go through blocks", ModuleCategory.RENDER);
    }

}
