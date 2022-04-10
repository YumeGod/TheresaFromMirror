package cn.loli.client.injection.mixins;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.NetworkManager$5")
public abstract class MixinNetworkManagerChInit {

    @Inject(method = "initChannel", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;pipeline()Lio/netty/channel/ChannelPipeline;", shift = At.Shift.AFTER), remap = false)
    private void onInitChannel(Channel p_initChannel_1_, CallbackInfo ci) {
        if (p_initChannel_1_ instanceof SocketChannel) {
        //    p_initChannel_1_.pipeline().addFirst(new HttpProxyHandler(new InetSocketAddress("0.0.0.0", 80)));
        }
        //
    }
}