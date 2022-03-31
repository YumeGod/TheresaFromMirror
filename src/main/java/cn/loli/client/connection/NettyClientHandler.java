package cn.loli.client.connection;

import cn.loli.client.Main;
import cn.loli.client.utils.misc.CrashUtils;
import cn.loli.client.utils.misc.RandomUtil;
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
import java.util.Base64;
import java.util.Objects;

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    CrashUtils crashUtils = new CrashUtils();
    String ping;
    int uid;
    int lastUid;
    final Base64.Encoder encoder = Base64.getEncoder();
    final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Main.INSTANCE.connected = true;
        uid = RandomUtil.getInstance().getRandomInteger(-37581, -25581);

        ctx.channel().writeAndFlush(new String(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey),
                PacketUtil.Type.PING, Main.INSTANCE.publicKey).pack().getBytes(), StandardCharsets.UTF_8));


        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Main.INSTANCE.hasKey) {
                    ping = "PING!" + crashUtils.AlphabeticRandom(40);
                    ctx.channel().writeAndFlush(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey), PacketUtil.Type.HEARTBEAT, ping).pack());
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (Main.INSTANCE.hasKey) {
                    if (uid != lastUid) {
                        String authcode = "paste-ware1337:" + uid + ":" + crashUtils.AlphabeticRandom(20);
                        ctx.channel().writeAndFlush(new Packet(new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey), PacketUtil.Type.HEARTBEAT,
                                "PING!:" + encoder.encodeToString(authcode.getBytes(StandardCharsets.UTF_8))).pack());
                        lastUid = uid;
                    }
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

                    if (i.contains("PING!:")) {
                        String time = new String(decoder.decode(i.split(":")[1]), StandardCharsets.UTF_8).split(":")[1];
                        //
                        if (new String(decoder.decode(i.split(":")[1]), StandardCharsets.UTF_8).split(":")[2].length() != 20) {
                            Main.INSTANCE.println("Keep Alive Broken " + "Yes I know it's a bad idea to use a spoofer like this but you are such a gay");
                            channelHandlerContext.close();
                            break;
                        }

                        try {
                            uid = (int) Math.pow(uid, 2) * 2;
                            if (!Objects.equals(uid, Integer.parseInt(time))) {
                                Main.INSTANCE.println("Keep Alive Broken " + "ordinal data error");
                                channelHandlerContext.close();
                                break;
                            }
                            uid = uid + RandomUtil.getInstance().getRandomInteger(-1337, 1337);
                        } catch (Exception e) {
                            Main.INSTANCE.println("Keep Alive Broken" + e.getMessage());
                            channelHandlerContext.close();
                            break;
                        }


                    } else {
                        if (!Objects.equals(i, ping)) {
                            Main.INSTANCE.println("Keep Alive Broken " + "ordinal data error x2");
                            channelHandlerContext.close();
                        }
                    }
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
        println("Connecting lost");
        Main.INSTANCE.guiScreen = Minecraft.getMinecraft().currentScreen;
        Main.INSTANCE.connected = false;
        ctx.channel().close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        println("Exception happened " + cause.getMessage());
        Main.INSTANCE.guiScreen = Minecraft.getMinecraft().currentScreen;
        Main.INSTANCE.connected = false;
        ctx.channel().close();
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
