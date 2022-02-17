package cn.loli.client.connection;

import cn.loli.client.Main;
import cn.loli.client.utils.HWIDUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Objects;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.INSTANCE.println("Client connected!");
        ctx.channel().writeAndFlush(new Packet(PacketUtil.Type.LOGIN, Main.INSTANCE.name + "|" + Main.INSTANCE.password + "|" + HWIDUtil.getHWID()).pack());
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.channel().writeAndFlush(new Packet(PacketUtil.Type.HEARTBEAT, "PING!").pack());
            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Packet p = PacketUtil.unpack(s);
        String result = "";
        if (p != null) {
//            Main.INSTANCE.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
            result = p.content;
            switch (p.type) {
                case LOGIN:
                    break;
                case COMMAND:
                    break;
                case HEARTBEAT:
                    break;
                case EXIT:
                    break;
                case MESSAGE:
                    if (Minecraft.getMinecraft().thePlayer != null) {
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(result));
                    }
                    break;
            }
        }
    }


}
