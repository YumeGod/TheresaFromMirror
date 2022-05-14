package cn.loli.client.module.modules.combat;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import dev.xix.property.impl.BooleanProperty;

public class KeepSprint extends Module {

    public final BooleanProperty fake = new BooleanProperty("FakeFov", true);

    public boolean modify = false;

    public KeepSprint() {
        super("KeepSprint", "You wont lose your sprint when you attack entity", ModuleCategory.COMBAT);
    }

}
