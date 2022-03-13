package me.superskidder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.superskidder.utils.Entity;
import me.superskidder.utils.KeyPair;
import me.superskidder.utils.RSAUtils;
import me.superskidder.utils.UserAuth;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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


    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    //get content from url
    public static String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);

        HttpURLConnection connection = createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0");

        String var6;
        try {
            String result;
            try {
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException var10) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    throw var10;
                }
            }

            result = IOUtils.toString(inputStream, Charsets.UTF_8);
            var6 = result;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return var6;
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

                System.out.println("Request for Login");

                if (info.length == 3) {
                    String result = performGetRequest(new URL("https://api.m0jang.org/auth.php?user=" + info[0] + "&pass=" + info[1] + "&hwid=" + info[2]));
                    System.out.println("Account-" + info[0] + " Password-" + info[1] + " Contact-" + info[2] + " IP-" + ctx.channel().remoteAddress() + " Time-" + sdf.format(new Date()));
                    System.out.println(result);

                    if (!result.contains("success")) {
                        System.out.println(info[0] + " Verify failed -> " + result);
                        ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + "Sorry, you failed the login, ERROR CODE " + result).pack());
                        ctx.close();
                    }
                } else {
                    ctx.close();
                }

                channelGroup.writeAndFlush(new Packet(p.user, PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + info[0] + " login successfully").pack());
                break;
            case AUTHORIZE:
                String[] strings = p.content.split("\\|");

                System.out.println("Request for Authorize");

                if (strings.length == 3) {
                    String result = performGetRequest(new URL("https://api.m0jang.org/auth.php?user=" + strings[0] + "&pass=" + strings[1] + "&hwid=" + strings[2]));
                    if (!result.contains("success")) {
                        ctx.close();
                    }
                } else {
                    ctx.close();
                }

                Server.INSTANCE.userAuth.giveAccess(p.user, true);
                ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.AUTHORIZE, p.user + p.user).pack());
                ctx.close();
                break;
            case COMMAND:
                break;
            case HEARTBEAT:
                String i = p.content.replace("PING", "PONG");
                ;
                ctx.channel().pipeline().writeAndFlush(new Packet(p.user, PacketUtil.Type.HEARTBEAT, i).pack());
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
