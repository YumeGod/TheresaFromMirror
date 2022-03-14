package cn.loli.client.connection;

import cn.loli.client.Main;
import cn.loli.client.gui.guiscreen.GuiReconnectIRC;
import cn.loli.client.utils.misc.CrashUtils;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.protection.HWIDUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    CrashUtils crashUtils = new CrashUtils();
    String ping;
    TimeHelper alive = new TimeHelper();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.INSTANCE.connected = true;
        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                PacketUtil.Type.PING, Main.INSTANCE.publicKey).pack().getBytes(), StandardCharsets.UTF_8));


        alive.reset();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Main.INSTANCE.hasKey) {
                    if (alive.hasReached(8000)) {
                        Main.INSTANCE.println("Timeout, Process Dead...");
                        Main.INSTANCE.doCrash();
                    }
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
                    alive.reset();

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

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        println("Connecting lost:" + ctx.channel().remoteAddress());
        Main.INSTANCE.connected = false;
    }


    public void println(String obj) {
        Class<?> systemClass = null;
        try {
            systemClass = Class.forName("java.lang.System");
            Field outField = null;
            try {
                outField = systemClass.getDeclaredField("out");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            Class<?> printStreamClass = Objects.requireNonNull(outField).getType();
            Method printlnMethod = printStreamClass.getDeclaredMethod("println", String.class);
            Object object = outField.get(null);
            printlnMethod.invoke(object, obj);
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
