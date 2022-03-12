package me.superskidder.utils;

import java.util.HashMap;
import java.util.Map;

public class Handler {

    Map<String, UserAuth> map;

    Map<String, Boolean> map2;

    public Handler() {
        this.map = new HashMap<>();
        this.map2 = new HashMap<>();
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

    public void giveAccess(String user, Boolean access) {
        map2.put(user, access);
    }

    public void removeAccess(String user) {
        map2.remove(user);
    }

    public boolean getAccess(String user) {
        return map2.get(user);
    }
}
