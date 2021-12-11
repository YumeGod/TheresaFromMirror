

package cn.loli.client.scripting.runtime.minecraft.client.network;

import cn.loli.client.scripting.runtime.minecraft.util.WrapperIChatComponent;
import net.minecraft.client.network.NetHandlerHandshakeMemory;

public class WrapperNetHandlerHandshakeMemory {
    private final NetHandlerHandshakeMemory real;

    public WrapperNetHandlerHandshakeMemory(NetHandlerHandshakeMemory var1) {
        this.real = var1;
    }

    public NetHandlerHandshakeMemory unwrap() {
        return this.real;
    }

    public void onDisconnect(WrapperIChatComponent var1) {
        this.real.onDisconnect(var1.unwrap());
    }
}
