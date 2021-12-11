

package cn.loli.client.scripting.runtime.minecraft.block.state;

import net.minecraft.block.state.BlockStateBase;

public class WrapperBlockStateBase {
    private final BlockStateBase real;

    public WrapperBlockStateBase(BlockStateBase var1) {
        this.real = var1;
    }

    public BlockStateBase unwrap() {
        return this.real;
    }

    public String toString() {
        return this.real.toString();
    }
}
