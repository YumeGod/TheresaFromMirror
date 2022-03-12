package cn.loli.client.connection;

import cn.loli.client.Main;
import cn.loli.client.utils.misc.CrashUtils;
import cn.loli.client.utils.protection.HWIDUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    CrashUtils crashUtils = new CrashUtils();
    String ping;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                PacketUtil.Type.PING, Main.INSTANCE.publicKey).pack().getBytes(), StandardCharsets.UTF_8));


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Main.INSTANCE.hasKey) {
                    ping = "PING!" + crashUtils.AlphabeticRandom(10);
                    ctx.channel().writeAndFlush(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey), PacketUtil.Type.HEARTBEAT, ping).pack());

                }
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
                        Main.INSTANCE.publicKey = RSAUtils.privateDecrypt(p.content, RSAUtils.getPrivateKey(Main.INSTANCE.privateKey));
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        Main.INSTANCE.doCrash();
                    }
                    Main.INSTANCE.hasKey = true;
                    channelHandlerContext.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                            PacketUtil.Type.LOGIN, (Main.INSTANCE.name + "|" + Main.INSTANCE.password + "|" + HWIDUtil.getHWID())).pack().getBytes(), StandardCharsets.UTF_8));

                    Main.INSTANCE.println("Client connected!");
                    break;
                case AUTHORIZE:
                        break;
                case LOGIN:
                case COMMAND:
                case HEARTBEAT:
                    String i = p.content.replace("PONG", "PING");

                    if (!Objects.equals(i, ping))
                        channelHandlerContext.close();
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
