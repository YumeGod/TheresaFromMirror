import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
        System.out.println("Client connected!");

        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Client.name, null, Client.hasKey),
                PacketUtil.Type.PING, Client.name).pack().getBytes(), StandardCharsets.UTF_8));


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Client.hasKey)
                    ctx.channel().writeAndFlush(new Packet(new Entity(Client.name, null, Client.hasKey), PacketUtil.Type.HEARTBEAT, "PING!").pack());

            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Packet p = PacketUtil.unpack(s);
        String result = "";
        if (p != null) {
            System.out.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
            result = p.content;
            switch (p.type) {
                case PONG:
                    try {
                        Client.publicKey = RSAUtils.getPublicKey(result);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    Client.hasKey = true;
                    channelHandlerContext.channel().writeAndFlush(new String(new Packet(new Entity(Client.name, null, Client.hasKey),
                            PacketUtil.Type.LOGIN, (Client.name + "|" + Client.password + "|" + Client.hwid)).pack().getBytes(), StandardCharsets.UTF_8));
                case LOGIN:
                case COMMAND:
                case HEARTBEAT:
                case EXIT:
                    break;
                case MESSAGE:
                    System.out.println(result);
                    break;
            }
        }
    }


}
