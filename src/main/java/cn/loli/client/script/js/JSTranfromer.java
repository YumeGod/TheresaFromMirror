package cn.loli.client.script.js;

import cn.loli.client.Main;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSTranfromer {

    private Invocable invocable;
    private String name,desc;

    public JSTranfromer(String source){
        ScriptEngine ee = new ScriptEngineManager().getEngineByName("Nashorn");
        Main.INSTANCE.println("[JSTranfromer] Loading script: " + source);
        try {
            ee.eval(source);
            this.name = (String) ee.get("name");
            this.desc = (String) ee.get("desc");
            this.invocable = (Invocable) ee;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
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
