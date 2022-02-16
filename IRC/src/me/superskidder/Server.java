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
import io.netty.handler.timeout.IdleStateHandler;
import me.superskidder.datebase.VisitMySql;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Server {
    public static ChannelFuture cf;
    public static int port;
    public static String datebase;
    public static String datebaseUserName;
    public static String datebasePassword;
    public static Map<String, String> ranks = new HashMap<>();

    public static void main(String[] args) {
        port = Integer.parseInt(args[0]);
        datebase = args[1];
        datebaseUserName = args[2];
        datebasePassword = args[3];
        String[] rs = args[4].split(";");
        for (String r : rs) {
            ranks.put(r.split(",")[0], r.split(",")[1]);
        }

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
                                socketChannel.pipeline().addLast("decoder", new StringDecoder());
                                socketChannel.pipeline().addLast("encoder", new StringEncoder());
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
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String s = sc.nextLine();
            if (s.equals("stop")) {
                System.exit(0);
            }
            if (s.startsWith("register ")) {
                String[] ss = s.split(" ");
                if (ss.length != 3) {
                    System.out.println("Invalid args!");
                } else {
                    VisitMySql.register(ss[1], ss[2]);
                }
            }
        }
    }
}
