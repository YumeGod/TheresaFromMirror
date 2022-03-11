package me.superskidder;

public class Packet {
    String content;
    PacketUtil.Type type;

    public Packet(PacketUtil.Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public String pack() {
        if (type == PacketUtil.Type.AUTH)
            return type.name() + "@SKID@" + content;

        return type.name() + "@SKID@" + content;
    }
}
