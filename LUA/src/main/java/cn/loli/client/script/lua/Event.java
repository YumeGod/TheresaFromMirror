package cn.loli.client.script.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.Map;

public class Event {
    public static void onUpdate(){
        // execute lua script update function
        for (Map.Entry<String, Globals> entry : LuaManager.INSTANCE.scripts.entrySet()) {
            Globals globals = entry.getValue();
            globals.get("onPreUpdate").call();
        }
    }
}
