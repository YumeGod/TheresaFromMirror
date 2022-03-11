package me.superskidder.utils;

import java.security.KeyPair;

public class UserAuth {

    Entity user;
    KeyPair keyPair;

    public UserAuth(Entity user, KeyPair keyPair) {
        this.user = user;
        this.keyPair = keyPair;
    }

    public Entity getUser() {
        return user;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }
}
