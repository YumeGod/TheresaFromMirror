package me.superskidder;

import me.superskidder.utils.RSAUtils;

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
                RSAUtils.publicEncrypt(type.name() + "@SKID@" + content, Server.INSTANCE.userAuth.get(user).getKeyPair().getPublic());
    }

    public String getContent() {
        return content;
    }

    public PacketUtil.Type getType() {
        return type;
    }

    public String getUser() {
        return user;
    }
}
