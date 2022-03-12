package me.loader;

import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;


public class Server {

    public Server(int port) throws IOException {
        System.out.println((Object) "Authorization Done...");
        ServerSocket tcpServer = new ServerSocket(port);
        System.out.println("Waiting for connection...");
        while (!tcpServer.isClosed()) {
            Socket socket = tcpServer.accept();
            Client client = new Client(socket);
            client.start();
        }
    }
}

class Client extends Thread {
    @NotNull
    public static final String PATH = "Theresa/";
    @NotNull
    public static final String CLIENT_PATH = "Theresa/Load.jar";

    @NotNull
    private final Socket clientSocket;

    public Client(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream input = null;
        DataOutputStream output = null;
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!clientSocket.isClosed()) {
            String received = null;
            try {
                received = Objects.requireNonNull(input).readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (clientSocket.getInetAddress().getHostAddress() == null)
                return;

            String ip = clientSocket.getInetAddress().getHostAddress();

            //The Authorized Stream
            if (Objects.equals(received, "HIHI")) {
                try {
                    Objects.requireNonNull(output).writeUTF("Passed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println((Object) ("Sending file to " + ip + "..."));
                try {
                    sendFile(clientSocket, output, System.nanoTime());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void sendFile(Socket socket, DataOutputStream output, long startTime) throws FileNotFoundException {
        DataInputStream fileStream = new DataInputStream(new BufferedInputStream(new FileInputStream(CLIENT_PATH)));
        int bufferSize = 8192;
        byte[] buf = new byte[bufferSize];
        try {
            int read;
            while ((read = fileStream.read(buf)) != -1) {
                output.write(buf, 0, read);
            }
            output.flush();
            fileStream.close();
            socket.close();
            StringBuilder stringBuilder = new StringBuilder().append("Finish sending file in ");
            String string = "%.3f";
            Object[] objectArray = new Object[]{(double) (System.nanoTime() - startTime) / 1.0E9};
            String string2 = String.format(string, Arrays.copyOf(objectArray, objectArray.length));
            System.out.println((Object) stringBuilder.append(string2).append(" seconds").toString());
        } catch (IOException e) {
            System.out.println((Object) "Connection reset, client disconnected");
        }
    }
}
