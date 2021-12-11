

package cn.loli.client.scripting.runtime.minecraft.client.network;

import cn.loli.client.scripting.runtime.minecraft.network.WrapperNetworkManager;
import cn.loli.client.scripting.runtime.minecraft.network.WrapperPacket;
import cn.loli.client.scripting.runtime.minecraft.util.WrapperIChatComponent;
import net.minecraft.client.network.NetHandlerPlayClient;

import java.util.Collection;
import java.util.UUID;

public class WrapperNetHandlerPlayClient {
    private final NetHandlerPlayClient real;

    public WrapperNetHandlerPlayClient(NetHandlerPlayClient var1) {
        this.real = var1;
    }

    public NetHandlerPlayClient unwrap() {
        return this.real;
    }

    public void cleanup() {
        this.real.cleanup();
    }

    public void onDisconnect(WrapperIChatComponent var1) {
        this.real.onDisconnect(var1.unwrap());
    }

    public void addToSendQueue(WrapperPacket var1) {
        this.real.addToSendQueue(var1.unwrap());
    }

    public WrapperNetworkManager getNetworkManager() {
        return new WrapperNetworkManager(this.real.getNetworkManager());
    }

    public Collection getPlayerInfoMap() {
        return this.real.getPlayerInfoMap();
    }

    public WrapperNetworkPlayerInfo getPlayerInfo(UUID var1) {
        return new WrapperNetworkPlayerInfo(this.real.getPlayerInfo(var1));
    }

    public WrapperNetworkPlayerInfo getPlayerInfo(String var1) {
        return new WrapperNetworkPlayerInfo(this.real.getPlayerInfo(var1));
    }

    public int getCurrentServerMaxPlayers() {
        return this.real.currentServerMaxPlayers;
    }

    public void setCurrentServerMaxPlayers(int var1) {
        this.real.currentServerMaxPlayers = var1;
    }
}
