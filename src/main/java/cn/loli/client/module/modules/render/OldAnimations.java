package cn.loli.client.module.modules.render;

import cn.loli.client.injection.mixins.MixinEntityPlayer;
import cn.loli.client.injection.mixins.MixinLayerHeldItem;
import cn.loli.client.injection.mixins.MixinRendererLivingEntity;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;

/**
 * Credit: asbyth (https://github.com/asbyth/Old-Animations/)
 * @see MixinEntityPlayer
 * @see MixinRendererLivingEntity
 * @see MixinLayerHeldItem
 */
public class OldAnimations extends Module {
    public BooleanValue oldSneaking = new BooleanValue("OldSneaking", true);
    public BooleanValue damageFlash = new BooleanValue("DamageFlash", true);
    public BooleanValue oldBlocking = new BooleanValue("OldBlocking", true);

    public OldAnimations() {
        super("OldAnimations", "Brings back the 1.7 animations.", ModuleCategory.RENDER);
    }
}
