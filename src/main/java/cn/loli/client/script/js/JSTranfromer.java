package cn.loli.client.script.js;

import cn.loli.client.Main;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSTranfromer {

    private final Invocable invocable;
    private final String name,desc;

    public JSTranfromer(String source){
        ScriptEngine ee = new ScriptEngineManager().getEngineByName("Nashorn");
        try {
            ee.eval(source);
        } catch (ScriptException e) {
            Main.INSTANCE.println("[JS] "+ "Error while loading script");
        }

        this.name = (String) ee.get("name");
        this.desc = (String) ee.get("version");
        this.invocable = (Invocable) ee;
    }

    public String getName(){
        return this.name;
    }

    public String getDesc(){
        return this.desc;
    }

    public Invocable getInvocable(){
        return this.invocable;
    }
}
