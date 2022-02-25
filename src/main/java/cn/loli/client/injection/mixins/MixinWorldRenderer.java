package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin(value = WorldRenderer.class)
public abstract class MixinWorldRenderer {

    @Shadow
    private IntBuffer rawIntBuffer;

    @Shadow
    private boolean noColor;


    @Shadow
    public abstract int getColorIndex(int p_78909_1_);



}
