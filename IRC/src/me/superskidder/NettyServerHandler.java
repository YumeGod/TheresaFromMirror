package me.superskidder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.superskidder.datebase.VisitMySql;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static me.superskidder.PacketUtil.unpack;

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
//        System.out.println("[DEBUG]"+o);
        Packet p = unpack(o);
        if (p != null) {
            System.out.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
            switch (p.type) {
                case LOGIN:
                    String[] info = p.content.split("\\|");
                    System.out.println(info[0] + "  -  " + info[1] + "  -  " + info[2]);
                    if (info.length == 3) {
                        String infos = VisitMySql.verify(info[0], info[1], info[2]);
                        System.out.println(infos);
                        if (infos.contains("Failed")) {
                            System.out.println(info[0] + " Verify failed");
                            ctx.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + "Sorry, " + infos).pack());
                            ctx.close();
                        }
                    } else {
                        ctx.close();
                    }
                    break;
                case COMMAND:
                    break;
                case HEARTBEAT:
                    if (Objects.equals(p.content, "PING!")) {
                        ctx.channel().pipeline().writeAndFlush(new Packet(PacketUtil.Type.HEARTBEAT, "PONG!").pack());
                    }
                    break;
                case EXIT:
                    break;
                case MESSAGE:
                    channelGroup.forEach(channel1 -> {
//                        if (channel1 != channel) {
                        channel.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + p.content).pack());
//                        } else {
//                            channel.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\2472" + p.content).pack());
//                        }
                    });
                    break;
            }
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
