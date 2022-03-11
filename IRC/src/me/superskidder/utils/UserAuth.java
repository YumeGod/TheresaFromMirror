package me.superskidder.utils;


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
