package cn.loli.client.connection;

import cn.loli.client.Main;

public class PacketUtil {

    public static Packet unpack(String content) {

        String[] strings = content.split("@NIGGA@");
        if (strings.length != 2) return null;

        Entity user = new Entity(Main.INSTANCE.name, null, Main.INSTANCE.hasKey);
        Packet i;

        try {
            i = new Packet(user, Type.valueOf(strings[1].split("@THERESA1337@")[0]), strings[1].split("@THERESA1337@")[1]);
            return i;
        } catch (Exception e) {
            i = null;
        }

        try {
            strings[1] = RSAUtils.privateDecrypt(strings[1], RSAUtils.getPrivateKey(Main.INSTANCE.privateKey));
            i = new Packet(user, Type.valueOf(strings[1].split("@THERESA1337@")[0]), strings[1].split("@THERESA1337@")[1]);
        } catch (Exception ignored) {
        }

        return i;
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