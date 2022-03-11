package me.superskidder.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {

    Map<String, UserAuth> map;

    public Handler() {
        this.map = new HashMap<>();
    }

    public void handle(String user, UserAuth access) {
        map.put(user, access);
    }

    public Map<String, UserAuth> getMap() {
        return map;
    }

    public UserAuth get(String user) {
        return map.get(user);
    }

    public void remove(String user) {
        map.remove(user);
    }

}
