package me.superskidder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import me.loader.Loader;
import me.superskidder.utils.Handler;
import me.superskidder.utils.SslOneWayContextFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Server {
    public static ChannelFuture cf;
    public static int port;

    public static Server INSTANCE;

    static Handler userAuth;
    static Loader loader;

    static SSLContext SERVER_CONTEXT;
    private static final File keyDir = new File("Theresa", "Keys");

    public Server() {
        INSTANCE = this;
    }

    public static void main(String[] args) {
        // 初始化Handle
        userAuth = new Handler();

        if (!keyDir.exists() && !keyDir.mkdirs())
            System.out.println("Failed to create key directory");

        new Thread(
                () -> {
                    try {
                        loader = new Loader(37721);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        ).start();

        //Split args and for smh
        port = Integer.parseInt(args[0]);

        //Start Server Thread
        File certificate = new File(keyDir, "m0jang_org.jks");  // 证书

        Thread thread = new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                String sChatPath = certificate.getAbsolutePath();
                                SSLEngine engine = SslOneWayContextFactory.getServerContext(sChatPath).createSSLEngine();
                                engine.setUseClientMode(false);//设置为服务器模式
                                socketChannel.pipeline().addLast("ssl", new SslHandler(engine));
                                socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
                                socketChannel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
                                socketChannel.pipeline().addLast(new IdleStateHandler(10, 20, 25, TimeUnit.SECONDS));
                                socketChannel.pipeline().addLast(new NettyServerHandler());
                            }
                        });

                try {
                    cf = bootstrap.bind(port).sync();
                    System.out.println("Server started!");
                    cf.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("Server closed!");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        thread.start();

        //Scanner for console (Maybe Improved Soon)
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            if (s.equals("stop")) {
                System.exit(0);
            }
        }
    }


}
