package me.superskidder.utils;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class Handler {

    Map<String, UserAuth> map;

    Map<Channel, String> map2;

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

    public Map<Channel, String> getChannelMap() {
        return map2;
    }


    public UserAuth get(String user) {
        return map.get(user);
    }

    public void remove(String user) {
        map.remove(user);
    }

    public void giveAccess(Channel channel, String access) {
        map2.put(channel, access);
    }

    public void removeAccess(Channel user) {
        map2.remove(user);
    }

    public String getName(Channel channel) {return map2.get(channel);}
}
