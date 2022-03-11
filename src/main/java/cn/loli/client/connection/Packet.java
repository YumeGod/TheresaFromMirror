package cn.loli.client.connection;

import cn.loli.client.Main;

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
            return user.getName() + "@NIGGA@" + type.name() + "@SKID@" + content;

        return user.getName() + "@NIGGA@" +
                RSAUtils.publicEncrypt(type.name() + "@SKID@" + content, Main.INSTANCE.publicKey);
    }
}
