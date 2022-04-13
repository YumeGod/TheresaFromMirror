import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
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
                e.printStackTrace();
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    //get content from url
    public static String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);

        HttpURLConnection connection = createUrlConnection(url);
        InputStream inputStream = null;
        connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0");

        String var6;
        try {
            String result;
            try {
                inputStream = connection.getInputStream();
                return IOUtils.toString(inputStream, Charsets.UTF_8);
            } catch (IOException var10) {
                IOUtils.closeQuietly(inputStream);
                inputStream = connection.getErrorStream();
                if (inputStream == null) {
                    throw var10;
                }
            }

            result = IOUtils.toString(inputStream, Charsets.UTF_8);
            var6 = result;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return var6;
    }


    private static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }
}
