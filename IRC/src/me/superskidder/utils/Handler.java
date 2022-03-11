package me.superskidder.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Handler {

    List<Entity> entity;
    Map<Entity, UserAuth> map;

    public Handler() {
        entity = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public void addEntity(Entity e) {
        entity.add(e);
    }

    public void removeEntity(Entity e) {
        entity.remove(e);
    }

    public Entity getEntity(String name) {
        for (Entity e : entity)
            if (e.getName().equals(name))
                return e;

        return null;
    }

    public void handle(Entity user, UserAuth access) {
        map.put(user, access);
    }

    public Map<Entity, UserAuth> getMap() {
        return map;
    }

    public UserAuth get(Entity user) {
        return map.get(user);
    }

    public void remove(Entity user) {
        map.remove(user);
    }

}
