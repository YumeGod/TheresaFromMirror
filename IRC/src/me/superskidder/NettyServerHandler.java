package me.superskidder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.superskidder.datebase.VisitMySql;
import me.superskidder.utils.RSAUtils;
import me.superskidder.utils.UserAuth;

import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static me.superskidder.PacketUtil.unpack;

public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent ent = (IdleStateEvent) evt;
            if (ent.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(sdf.format(new Date()) + ": You have heart disease");
                System.out.println("User" + ctx.channel().remoteAddress() + "Disconnected");
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String o) throws Exception {
        // Un Pack for decrypt
        Packet p = unpack(o);

        // Get the Packet Info
        if (p != null) {
            switch (p.type) {
                case PING:
                    Map<String, String> rsaKey = RSAUtils.createKeys(1024);
                    //TODO : Username
                    Server.INSTANCE.userAuth.handle("", new UserAuth("", new KeyPair(RSAUtils.getPublicKey(rsaKey.get("publicKey"))
                            , RSAUtils.getPrivateKey(rsaKey.get("privateKey")))));

                    break;
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


                    channelGroup.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + info[0] + " login successfully").pack());
                    break;
                case COMMAND:
                    break;
                case HEARTBEAT:
                    if (Objects.equals(p.content, "PING!")) {
                        ctx.channel().pipeline().writeAndFlush(new Packet(PacketUtil.Type.HEARTBEAT, "PONG!").pack());
                    }
                    break;
                case EXIT:
                    channelGroup.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + p.content).pack());
                    break;
                case MESSAGE:
//                    channelGroup.forEach(channel1 -> {
//                        if (channel1 != channel) {
                    channelGroup.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + p.content).pack());

//                        } else {
//                            channel.writeAndFlush(new Packet(PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\2472" + p.content).pack());
//                        }
//                    });
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
