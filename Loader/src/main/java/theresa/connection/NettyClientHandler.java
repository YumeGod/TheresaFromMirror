package theresa.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import theresa.Main;
import theresa.protection.HWIDUtil;
import theresa.protection.RSAUtils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                PacketUtil.Type.PING, Main.INSTANCE.publicKey).pack().getBytes(), StandardCharsets.UTF_8));

    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Packet p = PacketUtil.unpack(s);
        if (p != null) {
            switch (p.type) {
                case PONG:
                    try {
                        Main.INSTANCE.publicKey = RSAUtils.privateDecrypt(p.content, RSAUtils.getPrivateKey(Main.INSTANCE.privateKey));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        Main.INSTANCE.doCrash();
                    }
                    Main.INSTANCE.hasKey = true;
                    channelHandlerContext.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                            PacketUtil.Type.AUTHORIZE, (Main.INSTANCE.name + "|" + Main.INSTANCE.password + "|" + HWIDUtil.getHWID())).pack().getBytes(), StandardCharsets.UTF_8));

                    Main.INSTANCE.println("Client connected!");
                    break;
                case AUTHORIZE:
                    if (p.content.equals(Main.INSTANCE.name + Main.INSTANCE.name)) {
                        Main.INSTANCE.println("Client authorized!");
                    }
                        break;
            }
        }
    }


}
