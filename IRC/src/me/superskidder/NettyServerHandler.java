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
import me.superskidder.utils.Entity;
import me.superskidder.utils.KeyPair;
import me.superskidder.utils.RSAUtils;
import me.superskidder.utils.UserAuth;

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
        // unPack for decrypt
        Packet p = unpack(o);

        //Check if the Packet Available
        if (p == null)
            throw new Exception("Packet is null");

        // Get the Packet Info
        switch (p.type) {
            case PING:
                //init the map
                Map<String, String> rsaKey = RSAUtils.createKeys(1024);

                //Try to get the entity from the database
                System.out.println("Private Key for this boi: " + RSAUtils.getPrivateKey(rsaKey.get("privateKey")));

                Entity entity = new Entity(p.user);

                //Get the Key and put to map
                Server.INSTANCE.userAuth.handle(p.user, new UserAuth(entity, new KeyPair(RSAUtils.getPublicKey(p.content)
                        , RSAUtils.getPrivateKey(rsaKey.get("privateKey")))));

                //get Public Key (Dont Forget to re Get it)
                //Then send the Public Key to the Client
                ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.PONG, RSAUtils.publicEncrypt(rsaKey.get("publicKey"), Server.INSTANCE.userAuth.get(p.user).getKeyPair().getPublic())).pack());

                System.out.println("Successful for Handle");
                break;
            case LOGIN:
                //Get the Info
                String[] info = p.content.split("\\|");

                System.out.println("Account" + info[0] + "  Password-  " + info[1] +
                        "  Contact-  " + info[2] + "  IP-  " + ctx.channel().remoteAddress() + "  Time-  " + sdf.format(new Date()));

                System.out.println("Private Key for this boi: " + Server.userAuth.get(p.user).getKeyPair().getPrivate());

                if (info.length == 3) {
                    String verify = VisitMySql.verify(info[0], info[1], info[2]);
                    System.out.println(verify);
                    if (verify.contains("Failed")) {
                        System.out.println(info[0] + " Verify failed");
                        ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + "Sorry, " + verify).pack());
                        ctx.close();
                    }
                } else {
                    ctx.close();
                }

                channelGroup.writeAndFlush(new Packet(p.user, PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + info[0] + " login successfully").pack());
                break;
            case COMMAND:
                break;
            case HEARTBEAT:
                if (Objects.equals(p.content, "PING!"))
                    ctx.channel().pipeline().writeAndFlush(new Packet(p.user, PacketUtil.Type.HEARTBEAT, "PONG!").pack());
                else
                    ctx.close();
                break;
            case EXIT:
                Server.INSTANCE.userAuth.remove(p.user);
                System.out.println("Cya later " + p.user);
                ctx.close();
                break;
            case MESSAGE:
                channelGroup.writeAndFlush(new Packet(p.user, PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + p.content).pack());
                break;
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
