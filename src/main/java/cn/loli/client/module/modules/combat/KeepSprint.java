package cn.loli.client.module.modules.combat;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;

public class KeepSprint extends Module {

    public final BooleanValue fake = new BooleanValue("FakeFov", true);

    public boolean modify = false;

    public KeepSprint() {
        super("KeepSprint", "You wont lose your sprint when you attack entity", ModuleCategory.COMBAT);
    }

}
