package cn.loli.client.script.js;

import cn.loli.client.Main;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.function.Consumer;

public class JSTransformer {

    private Invocable invocable;
    private String name, desc;

    public JSTransformer(String source) {
        ScriptEngine engine;
        try {
            engine = new ScriptEngineManager(null).getEngineByName("nashorn");
            engine.eval(source);
            this.name = (String) engine.get("name");
            this.desc = (String) engine.get("desc");

            //re-define
            engine.put("log", (Consumer<String>) Main.INSTANCE::println);
            engine.put("sendMessage", (Consumer<String>) ChatUtils::info);
            engine.put("sendPacket", (Consumer<Packet<?>>) Minecraft.getMinecraft().getNetHandler().getNetworkManager()::sendPacket);
            engine.put("theWorld", Minecraft.getMinecraft().theWorld);
            engine.put("mc", Minecraft.getMinecraft());
            engine.put("thePlayer", Minecraft.getMinecraft().thePlayer);
            engine.put("PI", Math.PI);
            engine.put("boolVal", BooleanValue.class);
            engine.put("modeVal", ModeValue.class);
            engine.put("stringVal", StringValue.class);
            engine.put("colorVal", ColorValue.class);
            engine.put("numberVal", NumberValue.class);
            engine.put("value", Main.INSTANCE.valueManager);
            this.invocable = (Invocable) engine;
        } catch (ScriptException e) {
            Main.INSTANCE.println("脚本错误：" + e.getMessage());
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
