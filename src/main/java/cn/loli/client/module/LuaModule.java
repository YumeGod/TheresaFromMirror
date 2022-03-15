package cn.loli.client.module;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.UpdateEvent;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import org.luaj.vm2.Globals;

public class LuaModule extends Module {

    //TODO : 补全更多的方法

    public Globals globals;

    public LuaModule(String name, String description, Globals globals) {
        super(name, description, ModuleCategory.LUA);
        this.globals = globals;
        globals.get("init").call();
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        try {
            this.globals.get("on_update").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    private void onUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            try {
                this.globals.get("on_pre_update").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.globals.get("on_post_update").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (event.getEventType() == EventType.SEND) {
            try {
                this.globals.get("on_packet_post").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.globals.get("on_packet_receive").invoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        try {
            this.globals.get("on_tick").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget


    @Override
    public void onDisable() {
        try {
            this.globals.get("on_enable").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        try {
            this.globals.get("on_disable").invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
