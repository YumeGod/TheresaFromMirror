package cn.loli.client.connection;

import cn.loli.client.Main;

import java.security.interfaces.RSAPublicKey;

public class PacketUtil {

    public static Packet unpack(String content) {

        String[] strings = content.split("@NIGGA@");
        if (strings.length != 2) return null;

        Entity user = new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey);
        Packet i;

        try {
            i = new Packet(user, Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);
            return i;
        } catch (Exception e) {
            i = null;
        }

        try {
            strings[1] = RSAUtils.publicDecrypt(strings[1], (RSAPublicKey) Main.INSTANCE.publicKey);
            i = new Packet(user, Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return i;
    }


    public enum Type {
        LOGIN,
        PING,
        PONG,
        HEARTBEAT,
        EXIT,
        MESSAGE,
        COMMAND,
    }
}