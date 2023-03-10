

package cn.loli.client.injection.mixins;

import cn.loli.client.Main;
import cn.loli.client.events.PacketEvent;

import dev.xix.TheresaClient;
import dev.xix.event.EventType;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetAddress;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {

    @Inject(method = "channelRead0*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void packetReceived(ChannelHandlerContext p_channelRead0_1_, Packet packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(EventType.RECEIVE, packet);
        Main.INSTANCE.eventBus.call(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet packetIn, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(EventType.SEND, packetIn);
        Main.INSTANCE.eventBus.call(event);

        if (event.isCancelled()) ci.cancel();
    }

}
