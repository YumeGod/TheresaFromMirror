package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.script.Wrapper;
import cn.loli.client.utils.misc.ChatUtils;
import net.minecraft.client.Minecraft;

import javax.script.*;
import java.util.function.Consumer;

public class JSTransformer {

    private Invocable invocable;
    private String name, desc;

    public JSTransformer(String source) {
        ScriptEngine engine;
        try {
            engine = new ScriptEngineManager(null).getEngineByName("nashorn");
            //re-define
            engine.put("log", (Consumer<String>) Main.INSTANCE::println);
            engine.put("sendMessage", (Consumer<String>) ChatUtils::info);
            engine.put("world", Minecraft.getMinecraft().theWorld);
            engine.put("mc", Minecraft.getMinecraft());
            engine.put("player", Minecraft.getMinecraft().thePlayer);
            engine.put("PI", Math.PI);
            engine.put("value", Wrapper.getInstance());
            engine.eval(source);
            this.name = (String) engine.get("name");
            this.desc = (String) engine.get("desc");
            this.invocable = (Invocable) engine;
        } catch (ScriptException e) {
            Main.INSTANCE.println(e.getMessage());
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public Invocable getInvocable() {
        return this.invocable;
    }

}
