package me.superskidder.utils;

import java.util.HashMap;
import java.util.Map;

public class Handler {

    Map<String, UserAuth> map;

    public Handler() {
        this.map = new HashMap<>();
    }

    public void handle(String user, UserAuth access) {
        map.put(user, access);
    }

    public UserAuth get(String user) {
        return map.get(user);
    }

    public void remove(String user) {
        map.remove(user);
    }

}
