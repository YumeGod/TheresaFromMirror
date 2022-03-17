package cn.loli.client.script.js;

import cn.loli.client.Main;

import javax.script.*;

public class JSTransformer {

    private Invocable invocable;
    private String name, desc;

    public JSTransformer(String source) {
        ScriptEngine ee;
        try {
            ee = new ScriptEngineManager(null).getEngineByName("nashorn");
            ee.eval(source);
            this.name = (String) ee.get("name");
            this.desc = (String) ee.get("desc");
            this.invocable = (Invocable) ee;
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
