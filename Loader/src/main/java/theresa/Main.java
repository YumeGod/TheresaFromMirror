package theresa;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sun.misc.Unsafe;
import theresa.connection.NettyClientHandler;
import theresa.connection.Packet;
import theresa.connection.PacketUtil;
import theresa.protection.RSAUtils;

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
import java.util.Map;
import java.util.Objects;

public class Main {

    public static Main INSTANCE;

    public String privateKey;
    public String publicKey;

    public boolean hasKey;

    public String name, password;
    public static ChannelFuture cf;


    public Main() {
        INSTANCE = this;
    }

    public void IRC() {
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
                        }).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535));
                cf = bootstrap.connect("101.43.166.241", 9822).sync();
                println("Client started!");
                cf.channel().closeFuture().sync();
            } catch (InterruptedException ignored) {
            } finally {
                println("Client closed!");
                eventExecutors.shutdownGracefully();
                doCrash();
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

                    Map<String, String> keyMap = RSAUtils.createKeys(2048);
                    Main.INSTANCE.publicKey = keyMap.get("publicKey");
                    Main.INSTANCE.privateKey = keyMap.get("privateKey");
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
            println(String.valueOf(1 / 0));
            e.printStackTrace();
        }
    }

}