

package cn.loli.client.scripting;


import cn.loli.client.scripting.runtime.events.ScriptMotionUpdateEvent;
import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptModule extends Module {
    private ScriptEngine engine;

    ScriptModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        engine = scriptEngine;
    }


    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!getState()) return;
        try {
            ((Invocable) engine).invokeFunction("onRender2D");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }
    }

    @EventTarget
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (!getState()) return;
        ScriptMotionUpdateEvent ev = new ScriptMotionUpdateEvent(event.getEventType(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround());

        try {
            ((Invocable) engine).invokeFunction("onMotionUpdate", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        ev.apply(event);
    }
}
