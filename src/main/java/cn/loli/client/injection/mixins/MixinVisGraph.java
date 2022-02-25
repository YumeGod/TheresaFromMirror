package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.module.modules.render.Xray;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.Set;

@Mixin(VisGraph.class)
public abstract class MixinVisGraph {

    @Final
    @Shadow
    private final BitSet field_178612_d = new BitSet(4096);

    @Final
    @Shadow
    private static final int[] field_178613_e = new int[1352];

    @Shadow
    private int field_178611_f = 4096;

    @Shadow
    protected abstract Set<EnumFacing> func_178604_a(int p_178604_1_);

    /**
     * @author Mirror
     */
    @Overwrite
    public void func_178606_a(BlockPos pos) {

        // Client
        if (Main.INSTANCE.moduleManager.getModule(Xray.class).getState()) {
            return;
        }

        this.field_178612_d.set(getIndex(pos), true);
        --this.field_178611_f;
    }

    /**
     * @author Mirror
     */

    @Overwrite
    public SetVisibility computeVisibility() {
        SetVisibility setvisibility = new SetVisibility();

        // Client
        if (Main.INSTANCE.moduleManager.getModule(Xray.class).getState()) {
            setvisibility.setAllVisible(true);
            return setvisibility;
        }

        if (4096 - this.field_178611_f < 256) {
            setvisibility.setAllVisible(true);
        } else if (this.field_178611_f == 0) {
            setvisibility.setAllVisible(false);
        } else {
            for (int i : field_178613_e) {
                if (!this.field_178612_d.get(i)) {
                    setvisibility.setManyVisible(this.func_178604_a(i));
                }
            }
        }

        return setvisibility;
    }

    private static int getIndex(BlockPos pos) {
        return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    private static int getIndex(int x, int y, int z) {
        return x << 0 | y << 8 | z << 4;
    }
}
