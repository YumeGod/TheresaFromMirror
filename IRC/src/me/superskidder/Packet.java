package me.superskidder;

import me.superskidder.utils.Entity;
import me.superskidder.utils.RSAUtils;

import java.security.interfaces.RSAPrivateKey;

public class Packet {

    Entity user;
    String content;
    PacketUtil.Type type;

    public Packet(Entity user, PacketUtil.Type type, String content) {
        this.user = user;
        this.type = type;
        this.content = content;
    }

    public String pack() {
        if (!user.hasKey())
            return user.getName() + "@NIGGA@" + type.name() + "@SKID@" + user.getName();

        return user.getName() + "@NIGGA@" +
                RSAUtils.privateEncrypt(type.name() + "@SKID@" + content, (RSAPrivateKey) Server.INSTANCE.userAuth.get(user).getKeyPair().getPrivate());
    }
}
