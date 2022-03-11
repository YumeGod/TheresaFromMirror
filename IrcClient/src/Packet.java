public class Packet {
    String content;
    PacketUtil.Type type;

    public Packet(PacketUtil.Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public String pack() {
        return type.name() + "@SKID@" + content;
    }
}
