package cn.loli.client.protection;

import java.io.Serializable;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public final class KeyPair implements Serializable {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public KeyPair(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


    public RSAPublicKey getPublic() {
        return publicKey;
    }

    public RSAPrivateKey getPrivate() {
        return privateKey;
    }
}
