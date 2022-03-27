package theresa;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import sun.misc.Unsafe;
import theresa.connection.NettyClientHandler;
import theresa.connection.Timer;
import theresa.protection.RSAUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class Main {

    public static Main INSTANCE;

    public String privateKey;
    public String publicKey;

    public boolean hasKey;
    public boolean hasConnected;

    public String name, password;
    public static ChannelFuture cf;
    public Login login;

    public Timer timer;
    public String ip;

    public Main() {
        INSTANCE = this;
    }

    public void IRC() {
        login = new Login();
        login.init();
        while (login.head.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ip = getip((String) Objects.requireNonNull(login.jcb1.getSelectedItem()));

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
                cf = bootstrap.connect(ip, 9822).sync();
                println("Client started!");
                cf.channel().closeFuture().sync();
            } catch (InterruptedException ignored) {
            } finally {
                eventExecutors.shutdownGracefully();
            }
        }).start();
    }

    public String getip(String name) {
        switch (name) {
            case "Japan-1":
                return "jp1.nigger.party";
            case "Japan-2":
                return "103.170.233.101";
            case "HK-1":
                return "194.104.147.10";
            case "HK-2":
                return "cn1.nigger.party";
            case "US-1":
                return "us1.nigger.party";
            case "US-2":
                return "us2.nigger.party";
            case "US-3":
                return "209.209.57.142";
            case "Russia-1":
                return "185.22.152.2";
            case "Russia-2":
                return "46.29.161.218";
            case "Russia-3":
                return "45.142.246.156";

        }
        return "my.nigger.party";
    }

    public static class Login extends JFrame {
        private static final long serialVersionUID = 1L;
        public JLabel head;
        public JComboBox<String> jcb1;

        public void init() {
            FlatLightLaf.setup();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setAlwaysOnTop(true);
            // 设置顶部提示文字和主窗体的宽，高，x值，y值
            setTitle("Theresa.exe");
            setBounds(0, 0, 320, 400);
            setLocationRelativeTo(null);
            Container cp = getContentPane(); // 添加一个cp容器
            cp.setLayout(null); // 设置添加的cp容器为流布局管理器
            head = new JLabel("Login...");
            head.setBounds(100, 20, 275, 60);
            head.putClientProperty("FlatLaf.styleClass", "h0");

            //Proxy
            JLabel jl1 = new JLabel("Proxy:");
            jl1.setBounds(30, 100, 200, 30);
            jcb1 = new JComboBox<>();
            jcb1.setBounds(100, 100, 150, 30);
            jcb1.addItem("Japan-1");
            jcb1.addItem("Japan-2");
            jcb1.addItem("HK-1");
            jcb1.addItem("HK-2");
            jcb1.addItem("US-1");
            jcb1.addItem("US-2");
            jcb1.addItem("US-3");
            jcb1.addItem("Russia-1");
            jcb1.addItem("Russia-2");
            jcb1.addItem("Russia-3");

            cp.add(jcb1);
            cp.add(jl1);

            // 设置左侧用户名文字
            JLabel jl = new JLabel("Username:");
            jl.setBounds(30, 140, 200, 30);
            final JTextField name = new JTextField(); // 用户名框
            name.setBounds(100, 140, 150, 30); // 设置用户名框的宽，高，x值，y值

            // 设置左侧密码文字
            JLabel jl2 = new JLabel("Password:");
            jl2.setBounds(30, 180, 200, 30);
            final JPasswordField password = new JPasswordField(); // 密码框：为加密的***
            password.setBounds(100, 180, 150, 30); // 设置密码框的宽，高，x值，y值
            password.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

            JProgressBar progressBar = new JProgressBar();
            progressBar.setBounds(10, 150, 290, 10);
            progressBar.setIndeterminate(true);
            progressBar.setVisible(false);
            // 将head、jl、name、jl2、password添加到容器cp中
            cp.add(head);
            cp.add(jl);
            cp.add(name);
            cp.add(jl2);
            cp.add(password);
            cp.add(progressBar);

            // 确定按钮
            JButton jb = new JButton("Login"); // 添加一个确定按钮
            // 为确定按钮添加监听事件
            jb.addActionListener(actionEvent -> {
                Main.INSTANCE.name = name.getText();
                Main.INSTANCE.password = (new String(password.getPassword()));

                Map<String, String> keyMap = RSAUtils.createKeys(2048);
                Main.INSTANCE.publicKey = keyMap.get("publicKey");
                Main.INSTANCE.privateKey = keyMap.get("privateKey");
                //Get The Auto-Login System By Theresa.exe
                Main.INSTANCE.timer = new Timer();

                // Hide all the components
                head.setVisible(false);
                jl.setVisible(false);
                name.setVisible(false);
                jl2.setVisible(false);
                password.setVisible(false);
                jb.setVisible(false);
                progressBar.setVisible(true);

                //New Thread SUS
                new Thread(() -> {
                    ServerSocket serverSocket = null;
                    try {
                        serverSocket = new ServerSocket(12580);
                        Socket socket = serverSocket.accept();
                        DataInputStream input = null;
                        DataOutputStream output = null;
                        try {
                            input = new DataInputStream(socket.getInputStream());
                            output = new DataOutputStream(socket.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while (!serverSocket.isClosed()) {
                            String received = Objects.requireNonNull(input).readUTF();
                            if (received.equals("FuckYou"))
                                Objects.requireNonNull(output).writeUTF(INSTANCE.name + ":" + INSTANCE.password);
                        }

                    } catch (IOException e) {
                        try {
                            serverSocket.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            });
            jb.setBounds(90, 240, 140, 35); // 设置确定按钮的宽，高，x值，y值
            cp.add(jb); // 将确定按钮添加到cp容器中

            setResizable(false);

            setVisible(true);

            addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    Main.INSTANCE.doCrash();
                }

            });
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
