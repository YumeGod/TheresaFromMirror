

package cn.loli.client.scripting.runtime.minecraft.util;

import net.minecraft.util.Vector3d;

public class WrapperVector3d {
    private final Vector3d real;

    public WrapperVector3d(Vector3d var1) {
        this.real = var1;
    }

    public Vector3d unwrap() {
        return this.real;
    }

}
