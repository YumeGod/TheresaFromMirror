package cn.loli.client.module;

import cn.loli.client.events.UpdateEvent;
import com.darkmagician6.eventapi.EventTarget;
import org.luaj.vm2.Globals;

public class LuaModule extends Module {
    public Globals globals;

    public LuaModule(String name, String description,Globals globals) {
        super(name, description, ModuleCategory.LUA);
        this.globals = globals;
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        try {
            this.globals.get("onUpdate").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
