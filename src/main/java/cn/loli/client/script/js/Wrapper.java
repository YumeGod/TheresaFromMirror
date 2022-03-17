package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.value.Value;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {

    private static Wrapper wrapper;

    public static Wrapper getInstance() {
        if (wrapper == null) {
            wrapper = new Wrapper();
        }
        return wrapper;
    }

    //GOTO : ValueManager.java
    /**
     * @param name   The name of the owner
     * @param object The Value-object
     * this method will add the value to the owner if it doesn't exist
     */
    public void register(String name, @NotNull Value object) {
        List<Value> values = new ArrayList<>();
        if (Main.INSTANCE.valueManager.valueMap.get(name) != null)
            values = Main.INSTANCE.valueManager.valueMap.get(name);

        values.add(object);
        Main.INSTANCE.valueManager.valueMap.put(name, values);
    }
}
