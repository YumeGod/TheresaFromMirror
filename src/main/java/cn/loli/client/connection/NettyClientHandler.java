package cn.loli.client.connection;

import cn.loli.client.Main;
import cn.loli.client.utils.protection.HWIDUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Client connected!");

        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                PacketUtil.Type.PING, Main.INSTANCE.name).pack().getBytes(), StandardCharsets.UTF_8));


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Main.INSTANCE.hasKey)
                    ctx.channel().writeAndFlush(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey), PacketUtil.Type.HEARTBEAT, "PING!").pack());
            }
        }).start();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) {
        Packet p = PacketUtil.unpack(s);
        String result = "";
        if (p != null) {
            result = p.content;
            switch (p.type) {
                case PONG:
                    try {
                        Main.INSTANCE.publicKey = RSAUtils.getPublicKey(result);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    Main.INSTANCE.hasKey = true;
                    channelHandlerContext.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                            PacketUtil.Type.LOGIN, (Main.INSTANCE.name + "|" + Main.INSTANCE.password + "|" + HWIDUtil.getHWID())).pack().getBytes(), StandardCharsets.UTF_8));
                case LOGIN:
                case COMMAND:
                case HEARTBEAT:
                case EXIT:
                    break;
                case MESSAGE:
                    if (Minecraft.getMinecraft().thePlayer != null)
                        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(result));
                    break;
            }
        }
    }


}
