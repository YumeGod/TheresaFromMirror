package cn.loli.client.connection;

import cn.loli.client.Main;
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
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("LOGIN@SKID@" + Main.CLIENT_NAME + "|" + Main.CLIENT_VERSION, CharsetUtil.UTF_8));
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer("HEARTBEAT@SKID@PING!", CharsetUtil.UTF_8));
            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Packet p = PacketUtil.unpack(s);
        String result = "";
        if (p != null) {
            Main.INSTANCE.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
            switch (p.type) {
                case LOGIN:
                    result = s;
                    break;
                case COMMAND:
                    break;
                case HEARTBEAT:
                    if (Objects.equals(p.content, "PING!")) {
                        channelHandlerContext.writeAndFlush("PONG!");
                    }
                    break;
                case EXIT:
                    break;
                case MESSAGE:
                    break;
            }

            if (Minecraft.getMinecraft().thePlayer != null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(result));
            }
        }
    }


}
