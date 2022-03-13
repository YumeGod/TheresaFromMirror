package theresa;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sun.misc.Unsafe;
import theresa.connection.NettyClientHandler;
import theresa.protection.RSAUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Main extends Application {

    public static Main INSTANCE;

    public String privateKey;
    public String publicKey;

    public boolean hasKey;

    public String name, password;
    public static ChannelFuture cf;


    public Main() {
        INSTANCE = this;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Theresa.exe");
        stage.setScene(scene);
        stage.show();
    }

    public void IRC() {
        launch();
        while (Main.INSTANCE.name == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
                cf = bootstrap.connect("15.204.152.11", 9822).sync();
                println("Client started!");
                cf.channel().closeFuture().sync();
            } catch (InterruptedException ignored) {
            } finally {
                eventExecutors.shutdownGracefully();
            }
        }).start();
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
