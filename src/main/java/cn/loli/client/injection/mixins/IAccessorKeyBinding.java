

package cn.loli.client.injection.mixins;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface IAccessorKeyBinding {
    @Accessor("pressed")
    void setPressed(boolean pressed);
}
