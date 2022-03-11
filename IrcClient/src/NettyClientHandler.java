import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected!");
        ctx.channel().writeAndFlush(new String(new Packet(PacketUtil.Type.LOGIN, (Main.name + "|" + Main.password + "|" + Main.hwid)).pack().getBytes(), StandardCharsets.UTF_8));
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
            System.out.println("[DEBUG]" + p.type.name() + "  -  " + p.content);
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
                    System.out.println(result);
                    break;
            }
        }
    }


}
