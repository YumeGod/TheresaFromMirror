package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.render.Xray;
import cn.loli.client.utils.render.RenderUtils;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
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
    private int vertexCount;

    @Shadow
    private VertexFormat vertexFormat;

    @Shadow
    public abstract int getColorIndex(int p_78909_1_);


    /**
     * @author Theresa
     */

    @Overwrite
    public void putColorMultiplier(float red, float green, float blue, int vertexIndex) {
        int i = this.getColorIndex(vertexIndex);
        int j = -1;

        int j1; //R
        int k1; //G
        int l1; //B

        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                j1 = (int) ((float) (j & 255) * red);
                k1 = (int) ((float) (j >> 8 & 255) * green);
                l1 = (int) ((float) (j >> 16 & 255) * blue);
                j = j & -16777216;
                j = j | l1 << 16 | k1 << 8 | j1;
            } else {
                j1 = (int) ((float) (j >> 24 & 255) * red);
                k1 = (int) ((float) (j >> 16 & 255) * green);
                l1 = (int) ((float) (j >> 8 & 255) * blue);
                j = j & 255;
                j = j | j1 << 24 | k1 << 16 | l1 << 8;
            }


            if (Main.INSTANCE.moduleManager.getModule(Xray.class).getState()) {
                j = RenderUtils.getColor(j1, k1, l1, Main.INSTANCE.moduleManager.getModule(Xray.class).opacity.getObject());
            }
        }

        this.rawIntBuffer.put(i, j);
    }
}
