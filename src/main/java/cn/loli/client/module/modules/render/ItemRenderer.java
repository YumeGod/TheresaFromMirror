

package cn.loli.client.module.modules.render;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.renderer.GlStateManager;

public class ItemRenderer extends Module {
    private final NumberProperty<Float> translateX = new NumberProperty<>("TranslateX", 0f, -1f, 1f , 0.01f);
    private final NumberProperty<Float> translateY = new NumberProperty<>("TranslateY", 0.2f, -1f, 1f , 0.01f);
    private final NumberProperty<Float> translateZ = new NumberProperty<>("TranslateZ", 0f, -1f, 1f , 0.01f);
    private final NumberProperty<Float> scaleX = new NumberProperty<>("ScaleX", 1f, 0.1f, 5f , 0.01f);
    private final NumberProperty<Float> scaleY = new NumberProperty<>("ScaleY", 1f, 0.1f, 5f , 0.01f);
    private final NumberProperty<Float> scaleZ = new NumberProperty<>("ScaleZ", 1f, 0.1f, 5f , 0.01f);

    public ItemRenderer() {
        super("ItemRenderer", "Allows you to render your items the way you want to.", ModuleCategory.RENDER);
    }

    public void transform() {
        if (getState()) {
            GlStateManager.translate(translateX.getPropertyValue(), translateY.getPropertyValue(), translateZ.getPropertyValue());
            GlStateManager.scale(scaleX.getPropertyValue(), scaleY.getPropertyValue(), scaleZ.getPropertyValue());
        }
    }
}
