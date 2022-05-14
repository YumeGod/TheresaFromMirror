package cn.loli.client.module.modules.render;

import cn.loli.client.injection.mixins.MixinEntityPlayer;
import cn.loli.client.injection.mixins.MixinLayerHeldItem;
import cn.loli.client.injection.mixins.MixinRendererLivingEntity;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;

import dev.xix.property.impl.BooleanProperty;

/**
 * Credit: asbyth (https://github.com/asbyth/Old-Animations/)
 * @see MixinEntityPlayer
 * @see MixinRendererLivingEntity
 * @see MixinLayerHeldItem
 */
public class OldAnimations extends Module {
    public BooleanProperty oldSneaking = new BooleanProperty("OldSneaking", true);
    public BooleanProperty damageFlash = new BooleanProperty("DamageFlash", true);
    public BooleanProperty oldBlocking = new BooleanProperty("OldBlocking", true);

    public OldAnimations() {
        super("OldAnimations", "Brings back the 1.7 animations.", ModuleCategory.RENDER);
    }
}
