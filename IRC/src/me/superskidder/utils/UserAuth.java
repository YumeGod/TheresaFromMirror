package me.superskidder.utils;

import java.security.KeyPair;

public class UserAuth {

    String username;
    KeyPair keyPair;

    public UserAuth(String username, KeyPair keyPair) {
        this.username = username;
        this.keyPair = keyPair;
    }
}
