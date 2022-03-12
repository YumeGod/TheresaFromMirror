package theresa.connection;

import theresa.Main;
import theresa.protection.RSAUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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

        try {
            return user.getName() + "@NIGGA@" +
                    RSAUtils.publicEncrypt(type.name() + "@SKID@" + content, RSAUtils.getPublicKey(Main.INSTANCE.publicKey));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }

        return null;
    }

    public String getContent() {
       return content;
    }

    public PacketUtil.Type getType() {
        return type;
    }

    public Entity getUser() {
        return user;
    }

}
