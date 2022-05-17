

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.connection.NettyClientHandler;
import cn.loli.client.connection.RSAUtils;
import cn.loli.client.connection.SslOneWayContextFactory;
import cn.loli.client.events.*;
import cn.loli.client.file.FileManager;
import cn.loli.client.gui.guiscreen.GuiReconnectIRC;
import cn.loli.client.gui.ttfr.FontLoaders;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.protection.GuiCrashMe;
import cn.loli.client.protection.ProtectionThread;
import cn.loli.client.script.ScriptLoader;
import cn.loli.client.script.java.PluginsManager;
import cn.loli.client.script.java.sfontmanager.SFontLoader;
import cn.loli.client.utils.misc.ExploitFix;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.others.SoundFxPlayer;
import com.Kernel32;


import dev.xix.event.Event;
import dev.xix.event.bus.EventBus;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.PropertyManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import javax.net.ssl.SSLEngine;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.System.getProperty;


public class Main {
    @NotNull
    public static final String CLIENT_NAME = "theresa";
    @NotNull
    public static final String CLIENT_AUTHOR = ".Space";
    public static final double CLIENT_VERSION_NUMBER = 1.1;
    @NotNull
    public static final String CLIENT_VERSION = CLIENT_VERSION_NUMBER + "- Release";
    @NotNull
    public static final String CLIENT_INITIALS = "星";

    public FontLoaders fontLoaders;
    public static Main INSTANCE;
    public String name, password;

    public Logger logger;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public PropertyManager valueManager;
    public FileManager fileManager;

    public Queue<Packet<?>> packetQueue;
    TimeHelper ms = new TimeHelper();
    public long timing;

    //Position Record
    public int realPosX;
    public int realPosY;
    public int realPosZ;

    public int[] pos = new int[]{0, 120, 0};

    public ChannelFuture cf;


    public String privateKey;
    public String publicKey;

    public boolean hasKey;
    public boolean connected;
    public GuiScreen guiScreen;
    public ScriptLoader scriptLoader;
    public PluginsManager pluginsManager;
    public SFontLoader sFontLoader;

    public final EventBus<Event> eventBus;


    public Main() {
        INSTANCE = this;
        eventBus = new EventBus<>();
        eventBus.register(this);
    }

    public void startClient() {
        logger = LogManager.getLogger();

        if (getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows"))
            if (Kernel32.INSTANCE.IsDebuggerPresent()) {
                println("Dont be a sily gay");
                doCrash();
            }

        //Do Detect
        doDetect();
        //IRC
        IRC();

        //File Load
        fileManager = new FileManager();
        //Value Load
        valueManager = new PropertyManager();
        //Command init
        commandManager = new CommandManager();
        //Module init
        moduleManager = new ModuleManager();

        //Plugins and Scripts init
        pluginsManager = new PluginsManager();
        scriptLoader = new ScriptLoader();
        scriptLoader.init();

        //font loader
        fontLoaders = new FontLoaders();
        sFontLoader = new SFontLoader();

        //addCommands
        commandManager.addCommands();

        //addModules
        moduleManager.addModules();

        //files load
        fileManager.load();

        //Crasher
        packetQueue = new ConcurrentLinkedQueue<>();
        ms.reset();
        timing = 100L;

        //play sound when everything done
        new SoundFxPlayer().playSound(SoundFxPlayer.SoundType.SPECIAL, -8);
    }

    public void stopClient() {
        try {
            fileManager.save();
        } catch (Exception e) {
            System.err.println("Failed to save settings:");
            e.printStackTrace();
        }
    }

    private final IEventListener<UpdateEvent> onMisc = event -> {
        new ExploitFix(null);
    };

    private final IEventListener<PacketEvent> onWorldChange = event -> {
        if (event.getPacket() instanceof S07PacketRespawn || event.getPacket() instanceof S01PacketJoinGame) {
            packetQueue.clear();
            ms.reset();
        }

        new ExploitFix(event);
    };

    private final IEventListener<TickEvent> onTick = event -> {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiDownloadTerrain && (Minecraft.getMinecraft().thePlayer != null))
            Minecraft.getMinecraft().thePlayer.closeScreen();

        if (ms.hasReached(timing)) {
            if (!packetQueue.isEmpty()) Minecraft.getMinecraft().
                    getNetHandler().getNetworkManager().sendPacket(packetQueue.poll());
            ms.reset();
        }
    };

    private final IEventListener<TextEvent> nameFix = e -> {
        if (e.getText().contains("\247k"))
            e.setText(StringUtils.replace(e.getText(), "\247k", ""));
    };


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
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
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
            attack();
            println(String.valueOf(1 / 0));
            e.printStackTrace();
        }
    }

    protected void doDetect() {
        //Auth
        new Thread(() -> {
            try {
                while (true) {
                    if (!connected)
                        Minecraft.getMinecraft().displayGuiScreen(new GuiReconnectIRC(guiScreen));

                    if (ProtectionThread.getInstance().runChecks()) {
                        println("检测到非法行为，已自动踢出");
                        attack();
                        Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(new GuiCrashMe(),
                                "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Dont Skid")));
                        try {
                            Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog",
                                    java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"),
                                    null, "NO DEBUG PLZ? " + "\n" + "Debugging is just skidding with extra work ;)", "Theresa.exe", 0);
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                                 ClassNotFoundException e) {
                            doCrash();
                            attack();
                        }
                        doCrash();
                    }

                    Thread.sleep(5000L); //-5s
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    public Bootstrap bootstrap;
    public Thread thread;

    private void IRC() {
        doLogin();

        thread = new Thread(() -> {
            ircLogin("my.nigger.party");
        });

        thread.start();
    }

    public void ircLogin(String ip) {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            InputStream cChatPath = null;
                            try {
                                String home = System.getProperty("user.home");
                                File keyDir = new File(home, "Theresa");
                                File privateKeyFile = new File(keyDir, name);
                                cChatPath = new FileInputStream(privateKeyFile.getAbsolutePath());
                            } catch (FileNotFoundException e) {
                                println("Failed to load");
                                doCrash();
                            }
                            SSLEngine engine = SslOneWayContextFactory.getClientContext(cChatPath, "theresa" + name + "antileak")
                                    .createSSLEngine();
                            engine.setUseClientMode(true);//客户方模式
                            socketChannel.pipeline().addLast("ssl", new SslHandler(engine));
                            socketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                            socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));
                            socketChannel.pipeline().addLast(new NettyClientHandler());
                        }
                    });
            cf = bootstrap.connect(ip, 9822).sync();
            println("Connected");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException ignored) {
        } finally {
            println("Connecting lost");
        }
    }

    public void doLogin() {
        Socket socket = new Socket();
        try {
            socket = new Socket("127.0.0.1", 12580);
            socket.setSoTimeout(5000);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("FuckYou");
            while (!socket.isClosed()) {
                String received = Objects.requireNonNull(inputStream).readUTF();
                Main.INSTANCE.name = received.split(":")[0];
                Main.INSTANCE.password = received.split(":")[1];
                Map<String, String> keyMap = RSAUtils.createKeys(2048);
                Main.INSTANCE.publicKey = keyMap.get("publicKey");
                Main.INSTANCE.privateKey = keyMap.get("privateKey");
                socket.close();

                //Detect
                if (Main.INSTANCE.name == null || Main.INSTANCE.password == null)
                    Main.INSTANCE.doCrash();
            }
        } catch (IOException e) {
            try {
                socket.close();
                doCrash();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void attack() {
        Object[] o = null;
        while (true)
            o = new Object[]{o};

    }

}
