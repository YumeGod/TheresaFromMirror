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
        String packet = null;

        try {
            packet = PacketUtil.Type.PONG.equals(type) ? user + "@NIGGA@" + type.name() + "@SKID@" + content: user + "@NIGGA@" +
                    RSAUtils.publicEncrypt(type.name() + "@SKID@" + content, Server.INSTANCE.userAuth.get(user).getKeyPair().getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packet;
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
