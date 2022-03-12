package me.superskidder;

import me.superskidder.utils.RSAUtils;

import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

public class PacketUtil {

    public static Packet unpack(String content) {
        System.out.println("Unpacking packet with content: " + content);
        String[] strings = content.split("@NIGGA@");
        if (strings.length != 2) return null;

        Packet packet;

        try {
            packet = new Packet(strings[0],
                    Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);

            System.out.println("1. packet: " + packet.content);
        } catch (Throwable e) {
            packet = null;
        }

        try {
            if (packet == null) {
                System.out.println(Arrays.toString(Server.INSTANCE.userAuth.getMap().keySet().toArray()));
                RSAPrivateKey privateKey = Server.INSTANCE.userAuth.get(strings[0]).getKeyPair().getPrivate();
                strings[1] = RSAUtils.privateDecrypt(strings[1], privateKey);
                packet = new Packet(strings[0], Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);
                System.out.println("2 packet: " + packet.content);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return packet;
    }


    public enum Type {
        LOGIN,
        PING,
        PONG,
        AUTHORIZE,
        HEARTBEAT,
        EXIT,
        MESSAGE,
        COMMAND,
    }
}
