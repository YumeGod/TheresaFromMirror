package cn.loli.client.injection.mixins;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FontRenderer.class)
public interface IAccessorFontRenderer {
    @Accessor("colorCode")
    int[] getColorCode();

}
