

package cn.loli.client;

import cn.loli.client.command.CommandManager;
import cn.loli.client.connection.NettyClientHandler;
import cn.loli.client.events.LoopEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TextEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.file.FileManager;
import cn.loli.client.gui.ttfr.FontLoaders;
import cn.loli.client.module.ModuleManager;
import cn.loli.client.protection.GuiCrashMe;
import cn.loli.client.protection.ProtectionThread;
import cn.loli.client.utils.misc.ExploitFix;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.others.SoundFxPlayer;
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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    public static final double CLIENT_VERSION_NUMBER = 1.0;
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
    public ValueManager valueManager;
    private FileManager fileManager;

    public Queue<Packet<?>> packetQueue;
    TimeHelper ms = new TimeHelper();
    public long timing;

    //Position Record
    public int realPosX;
    public int realPosY;
    public int realPosZ;
    public static ChannelFuture cf;

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

        //IRC
        IRC();

        fileManager = new FileManager();
        valueManager = new ValueManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();

        commandManager.addCommands();
        moduleManager.addModules();
        fileManager.load();

//        moduleManager.getModule(ClickGUIModule.class).createClickGui();
        fontLoaders = new FontLoaders();

        //Auth
        doDetect();

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

    @EventTarget
    private void nameFix(TextEvent e) {
        if (e.getText().contains("\247k"))
            e.setText(StringUtils.replace(e.getText(), "\247k", ""));
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
                            attack();
                        }
                        doCrash();
                    }
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    private void IRC() {
        doLogin();
        new Thread(() -> {
            EventLoopGroup eventExecutors = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                socketChannel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
                                socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));

                                socketChannel.pipeline().addLast(new NettyClientHandler());
                            }
                        });
                cf = bootstrap.connect("101.43.166.241", 9822).sync();
//                cf = bootstrap.connect("127.0.0.1", 9822).sync();
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
                            attack();
                        }
                    }
                }).start();
            }
        }).start();
    }

    public static class Login extends JFrame {
        private static final long serialVersionUID = 1L;

        public void init() {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setAlwaysOnTop(true);
            // 设置顶部提示文字和主窗体的宽，高，x值，y值
            setTitle("Theresa.exe - Verify");
            setBounds(0, 0, 275, 175);
            setLocationRelativeTo(null);
            Container cp = getContentPane(); // 添加一个cp容器
            cp.setLayout(null); // 设置添加的cp容器为流布局管理器

            // 设置左侧用户名文字
            JLabel jl = new JLabel("用户名：");
            jl.setBounds(30, 10, 200, 35);
            final JTextField name = new JTextField(); // 用户名框
            name.setBounds(100, 10, 150, 35); // 设置用户名框的宽，高，x值，y值

            // 设置左侧密码文字
            JLabel jl2 = new JLabel("密码：");
            jl2.setBounds(30, 50, 200, 35);
            final JPasswordField password = new JPasswordField(); // 密码框：为加密的***
            password.setBounds(100, 50, 150, 35); // 设置密码框的宽，高，x值，y值

            // 将jl、name、jl2、password添加到容器cp中
            cp.add(jl);
            cp.add(name);
            cp.add(jl2);
            cp.add(password);

            // 确定按钮
            JButton jb = new JButton("确定"); // 添加一个确定按钮
            jb.addActionListener(new ActionListener() { // 为确定按钮添加监听事件

                public void actionPerformed(ActionEvent arg0) {
                    Main.INSTANCE.name = name.getText();
                    Main.INSTANCE.password = (new String(password.getPassword()));

                    setVisible(false);
                }
            });
            jb.setBounds(10, 100, 250, 30); // 设置确定按钮的宽，高，x值，y值
            cp.add(jb); // 将确定按钮添加到cp容器中

            setResizable(false);

            setVisible(true);

            addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    FMLCommonHandler.instance().exitJava(0, true);
                }

            });
        }
    }

    public void doLogin() {
        Login login = new Login();
        login.init();
        while (login.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }


    public void attack() {
        Object[] o = null;
        while (true) {
            o = new Object[]{o};
        }
    }

}
