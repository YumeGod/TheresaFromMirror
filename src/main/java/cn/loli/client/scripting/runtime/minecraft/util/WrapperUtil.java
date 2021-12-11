

package cn.loli.client.scripting.runtime.minecraft.util;

import net.minecraft.util.Util;

public class WrapperUtil {
    private final Util real;

    public WrapperUtil(Util var1) {
        this.real = var1;
    }

    public Util unwrap() {
        return this.real;
    }
}
