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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.applet.Main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public static String getStatus(String url) {
        final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        httpGet.setHeader("xf-api-key", "LnM-qSeQqtJlJmJnVt76GhU-SoiolWs9");
        String result = null;
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            result = IOUtils.toString(closeableHttpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
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
                String[] split = p.content.split("\\|");

                System.out.println("Request for Login");

                if (split.length == 3) {
                    String url = "https://api.m0jang.org/auth.php?user=" + split[0] + "&pass=" + split[1] + "&hwid=" + split[2];
                    String result = getStatus(url);

                    if (result == null) {
                        System.out.println("Authorize Failed");
                        ctx.close();
                    } else {
                        if (!result.contains("success")) {
                            System.out.println(split[0] + " Verify failed -> " + result);
                            ctx.close();
                        } else {
                            Server.INSTANCE.userAuth.giveAccess(ctx.channel(), p.user);
                            System.out.println(split[0] + " Verify Successful");

                            for (Channel i : Server.INSTANCE.userAuth.getChannelMap().keySet())
                                if (i.isOpen() && Server.INSTANCE.userAuth.getName(i) != null)
                                    i.writeAndFlush(new Packet(Server.INSTANCE.userAuth.getName(i), PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + split[0] + " login successfully").pack());
                        }
                    }
                } else {
                    System.out.println("Illegal Request");
                    ctx.close();
                }

                break;
            case AUTHORIZE:
                String[] strings = p.content.split("\\|");
                System.out.println("Request for Authorize");

                if (strings.length == 3) {
                    String url = "https://api.m0jang.org/auth.php?user=" + strings[0] + "&pass=" + strings[1] + "&hwid=" + strings[2];
                    String result;
                    result = getStatus(url);

                    if (result == null) {
                        System.out.println("Authorize Failed");
                        ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.AUTHORIZE, "AuthFailed").pack());
                        ctx.close();
                    } else {
                        if (!result.contains("success")) {
                            System.out.println("Dont Pass -> " + result);
                            ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.AUTHORIZE, "YouSuchANigger").pack());
                            ctx.close();
                        } else {
                            System.out.println("Success to Login");
                            ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.AUTHORIZE, p.user + p.user).pack());
                            ctx.close();
                        }
                    }
                } else {
                    System.out.println("Illegal Request");
                    ctx.writeAndFlush(new Packet(p.user, PacketUtil.Type.AUTHORIZE, "YouSuchABITCH").pack());
                    ctx.close();
                }

                break;
            case COMMAND:
                break;
            case HEARTBEAT:
                String i = p.content.replace("PING", "PONG");
                ctx.channel().pipeline().writeAndFlush(new Packet(p.user, PacketUtil.Type.HEARTBEAT, i).pack());
                break;
            case EXIT:
                Server.INSTANCE.userAuth.remove(p.user);
                System.out.println("Cya later " + p.user);
                ctx.close();
                break;
            case MESSAGE:
                for (Channel channel : Server.INSTANCE.userAuth.getChannelMap().keySet())
                    if (channel.isOpen() && Server.INSTANCE.userAuth.getName(channel) != null)
                        channel.writeAndFlush(new Packet(Server.INSTANCE.userAuth.getName(channel), PacketUtil.Type.MESSAGE, "\2476" + "[Theresa IRC]" + "\247r" + p.content).pack());
                break;
        }

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("Client " + channel.remoteAddress() + " connected");
        channelGroup.writeAndFlush(sdf.format(new Date()) + ": Client " + ctx.channel().remoteAddress() + " joined IRC.");
        channelGroup.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("Client " + channel.remoteAddress() + " removed");
        channelGroup.writeAndFlush(sdf.format(new Date()) + ": Client " + ctx.channel().remoteAddress() + " exit IRC.");
        Server.INSTANCE.userAuth.removeAccess(ctx.channel());
        channelGroup.remove(channel);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
