

package cn.loli.client.module.modules.render;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.NumberValue;
import net.minecraft.client.renderer.GlStateManager;

public class ItemRenderer extends Module {
    private final NumberValue<Float> translateX = new NumberValue<>("TranslateX", 0f, -1f, 1f);
    private final NumberValue<Float> translateY = new NumberValue<>("TranslateY", 0.2f, -1f, 1f);
    private final NumberValue<Float> translateZ = new NumberValue<>("TranslateZ", 0f, -1f, 1f);
    private final NumberValue<Float> scaleX = new NumberValue<>("ScaleX", 1f, 0.1f, 5f);
    private final NumberValue<Float> scaleY = new NumberValue<>("ScaleY", 1f, 0.1f, 5f);
    private final NumberValue<Float> scaleZ = new NumberValue<>("ScaleZ", 1f, 0.1f, 5f);

    public ItemRenderer() {
        super("ItemRenderer", "Allows you to render your items the way you want to.", ModuleCategory.RENDER);
    }

    public void transform() {
        if (getState()) {
            GlStateManager.translate(translateX.getObject(), translateY.getObject(), translateZ.getObject());
            GlStateManager.scale(scaleX.getObject(), scaleY.getObject(), scaleZ.getObject());
        }
    }
}
