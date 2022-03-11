import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

public class PacketUtil {

    public static Packet unpack(String content) {
        System.out.println(content);

        String[] strings = content.split("@NIGGA@");
        if (strings.length != 2) return null;

        Entity user = new Entity(Client.name, null, Client.hasKey);

        if (!user.hasKey())
            return new Packet(user, Type.valueOf(strings[1].split("@SKID@")[0]), strings[1].split("@SKID@")[1]);

        strings[1] = RSAUtils.publicDecrypt(strings[1], (RSAPublicKey) Client.publicKey);

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