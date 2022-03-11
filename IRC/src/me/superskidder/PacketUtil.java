package me.superskidder;

import me.superskidder.utils.Entity;
import me.superskidder.utils.RSAUtils;

import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

public class PacketUtil {

    public static Packet unpack(String content) {
        String[] strings = content.split("@NIGGA@");
        if (strings.length != 2) return null;

        Entity user = null;

        for (Entity i : Server.INSTANCE.userAuth.getMap().keySet())
            if (Objects.equals(i.getName(), strings[0]))
                user = i;

        if (user == null)
            return null;

        if (!user.hasKey())
            return new Packet(user, Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);

        strings[1] = RSAUtils.privateDecrypt(strings[1], (RSAPrivateKey) Server.INSTANCE.userAuth.get(user).getKeyPair().getPrivate());

        return new Packet(user, Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);
    }


    enum Type {
        LOGIN,
        PING,
        PONG,
        HEARTBEAT,
        EXIT,
        MESSAGE,
        COMMAND,
    }
}
