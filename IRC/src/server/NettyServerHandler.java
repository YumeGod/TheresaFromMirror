package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import server.datebase.VisitMySql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static server.PacketUtil.unpack;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent ent = (IdleStateEvent) evt;
            switch (ent.state()) {
                case READER_IDLE:
                    ctx.writeAndFlush(sdf.format(new Date()) + ": You have heart disease");
                    System.out.println("User" + ctx.channel().remoteAddress() + "Disconnected");
                    ctx.close();
                    break;
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String o) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(channel1 -> {
            if (channel1 != channel) {
                channel.writeAndFlush(o);
            } else {
                channel.writeAndFlush(o);
            }
        });
//        System.out.println("[DEBUG]"+o);
        Packet p = unpack(o);
        if (p != null) {
            System.out.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
            switch (p.type) {
                case LOGIN:
                    String[] info = p.content.split("\\|");
                    if (!VisitMySql.verify(info[0], info[1])) {
                        System.out.println("Verify failed");
                        ctx.close();
                    }
                    break;
                case COMMAND:
                    break;
                case HEARTBEAT:
                    if (Objects.equals(p.content, "PING!")) {
//                        ctx.writeAndFlush("PONG!");
                        ctx.channel().pipeline().writeAndFlush("PONG!");
                    }
                    break;
                case EXIT:
                    break;
                case MESSAGE:
                    break;
            }


        } else {
            System.out.println("[Packet Exception]" + ctx.channel().remoteAddress() + "    " + o);
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(sdf.format(new Date()) + ": Client " + ctx.channel().remoteAddress() + " joined IRC.");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(sdf.format(new Date()) + ": Client " + ctx.channel().remoteAddress() + " exit IRC.");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
