

package cn.loli.client.module.modules.render;

import cn.loli.client.injection.mixins.MixinItemRenderer;
import cn.loli.client.injection.mixins.MixinMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;

/**
 * @see MixinItemRenderer
 * @see MixinMinecraft
 */
public class BlockHit extends Module {
    public final BooleanValue animationsOnly = new BooleanValue("AnimationsOnly", false);

    public BlockHit() {
        super("BlockHit", "Adds back the 1.7 hitting mechanics.", ModuleCategory.RENDER);
    }
}
