import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

public class Client {
    public static ChannelFuture cf;

    static String name;
    static String password;
    static String hwid;
    static RSAPublicKey publicKey;
    static boolean hasKey;
    static Entity.State state = Entity.State.USER;

    public static void main(String[] args) {
        name = "VanillaMirror";
        password = "hazenova3C";

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
                        Objects.requireNonNull(output).writeUTF(name + ":" + password);
                }

            } catch (IOException e) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

}
