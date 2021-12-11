

package cn.loli.client.scripting.runtime.minecraft.network;

import net.minecraft.network.INetHandler;

public class WrapperINetHandler {
    private final INetHandler real;

    public WrapperINetHandler(INetHandler var1) {
        this.real = var1;
    }

    public INetHandler unwrap() {
        return this.real;
    }
}
