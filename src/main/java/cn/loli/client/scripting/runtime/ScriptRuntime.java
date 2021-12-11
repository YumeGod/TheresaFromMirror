

package cn.loli.client.scripting.runtime;

import cn.loli.client.scripting.runtime.minecraft.client.WrapperMinecraft;

public class ScriptRuntime {

    public static WrapperMinecraft getMinecraft() {
        return WrapperMinecraft.getMinecraft();
    }

}
