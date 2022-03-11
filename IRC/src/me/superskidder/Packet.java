package me.superskidder;

import me.superskidder.utils.Entity;
import me.superskidder.utils.RSAUtils;

import java.security.interfaces.RSAPrivateKey;

public class Packet {

    String user;
    String content;
    PacketUtil.Type type;

    public Packet(String user, PacketUtil.Type type, String content) {
        this.user = user;
        this.type = type;
        this.content = content;
    }

    public String pack() {
        if (PacketUtil.Type.PONG.equals(type))
            return user + "@NIGGA@" + type.name() + "@SKID@" + content;

        return user + "@NIGGA@" +
                RSAUtils.privateEncrypt(type.name() + "@SKID@" + content, Server.INSTANCE.userAuth.get(user).getKeyPair().getPrivate());
    }
}
