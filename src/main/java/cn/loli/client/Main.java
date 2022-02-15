

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.connection.NettyClientHandler;
import cn.loli.client.events.LoopEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.file.FileManager;
import cn.loli.client.gui.ttfr.FontLoaders;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.module.modules.misc.ClickGUIModule;
import cn.loli.client.protection.GuiCrashMe;
import cn.loli.client.protection.ProtectionThread;
import cn.loli.client.utils.*;
import cn.loli.client.value.ValueManager;
import com.Kernel32;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.getProperty;


public class Main {
    @NotNull
    public static final String CLIENT_NAME = "theresa";
    @NotNull
    public static final String CLIENT_AUTHOR = "CuteMirror";
    public static final double CLIENT_VERSION_NUMBER = -1;
    @NotNull
    public static final String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "-BETA";
    @NotNull
    public static final String CLIENT_INITIALS = "星";

    public static FontLoaders fontLoaders;

    public static Main INSTANCE;

    public Logger logger;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public ValueManager valueManager;
    private FileManager fileManager;

    public Queue<Packet<?>> packetQueue;
    TimeHelper ms = new TimeHelper();
    public long timing;

    //Position Record
    public int realPosX;
    public int realPosY;
    public int realPosZ;

    public Main() {
        INSTANCE = this;
        EventManager.register(this);
    }

    public void startClient() {
        logger = LogManager.getLogger();


        if (getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")) {
            if (Kernel32.INSTANCE.IsDebuggerPresent()) {
                println("Dont be a sily gay");
                doCrash();
                return;
            }
        } else {
            println("Gay");
        }

        try {
            if (HttpUtil.performGetRequest
                    (new URL("https://pastebin.com/raw/T0XipKMF")).contains("i am a man"))
                println("无论前方艰险如何 我都会在你身边");
            else
                doCrash();
        } catch (IOException e) {
            doCrash();
        }

        fileManager = new FileManager();
        valueManager = new ValueManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();

        commandManager.addCommands();
        moduleManager.addModules();
        fileManager.load();

//        moduleManager.getModule(ClickGUIModule.class).createClickGui();
        fontLoaders = new FontLoaders();

        //IRC
        IRC();

        //Auth
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(1000L); //-1s
                    if (ProtectionThread.getInstance().runChecks()) {
                        println("检测到非法行为，已自动踢出");
                        if (!(Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected))
                            Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(new GuiDisconnected(new GuiDisconnected(new GuiDisconnected(new GuiCrashMe(),
                                    "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "HAHAHAH")),
                                    "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "AS WHAT I SAY")),
                                    "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "NIGGA")),
                                    "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Dont Skid")));
                        try {
                            Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog",
                                    java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"),
                                    null, "NO DEBUG PLZ? " + "\n" + "Debugging is just skidding with extra work ;)", "Theresa.exe", 0);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                            doCrash();
                        }
                        doCrash();
                    }
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();

        //Crasher
        packetQueue = new ConcurrentLinkedQueue<>();
        ms.reset();
        timing = 100L;

        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.SPECIAL, -2);
    }

    public void stopClient() {
        try {
            fileManager.save();
        } catch (Exception e) {
            System.err.println("Failed to save settings:");
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onMisc(UpdateEvent e) {
        new ExploitFix(null);
    }

    @EventTarget
    private void onWorldChange(PacketEvent e) {
        if (e.getPacket() instanceof S07PacketRespawn || e.getPacket() instanceof S01PacketJoinGame) {
            packetQueue.clear();
            ms.reset();
        }

        new ExploitFix(e);
    }

    @EventTarget
    private void onTick(LoopEvent e) {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted < 5)
            return;

        if (ms.hasReached(timing)) {
            if (!packetQueue.isEmpty()) Minecraft.getMinecraft().
                    getNetHandler().getNetworkManager().sendPacket(packetQueue.poll());
            ms.reset();
        }
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

    public void doCrash() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = null;
            try {
                unsafe = (Unsafe) field.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Class<?> cacheClass = null;
            try {
                cacheClass = Class.forName("java.lang.Integer$IntegerCache");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Field cache = cacheClass.getDeclaredField("cache");
            long offset = unsafe.staticFieldOffset(cache);

            unsafe.putObject(Integer.getInteger("SkidSense.pub NeverDie"), offset, null);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void IRC() {
        new Thread(() -> {
            EventLoopGroup eventExecutors = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast("decoder", new StringDecoder());
                                socketChannel.pipeline().addLast("encoder", new StringEncoder());
                                socketChannel.pipeline().addLast(new NettyClientHandler());
                            }
                        });
                ChannelFuture cf = bootstrap.connect("101.43.166.241", 9822).sync();
                println("Client started!");
                cf.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                println("Client closed!");
                eventExecutors.shutdownGracefully();
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(100L);
                            if (!(Minecraft.getMinecraft().currentScreen instanceof GuiDisconnected))
                                Minecraft.getMinecraft().displayGuiScreen((new GuiDisconnected(new GuiCrashMe(),
                                        "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "服务器校检失败 请重新启动客户端"))));
                        } catch (InterruptedException e) {
                            doCrash();
                        }
                    }
                }).start();
            }
        }).start();
    }
}
