package server;

public class PacketUtil {

    public static Packet unpack(String content) {
        String[] strings = content.split("@SKID@");
        if(strings.length != 2){
            return null;
        }
        return new Packet(Type.valueOf(strings[0]), strings[1]);
    }


    enum Type {
        LOGIN,
        HEARTBEAT,
        EXIT,
        MESSAGE,
        COMMAND,
    }
}