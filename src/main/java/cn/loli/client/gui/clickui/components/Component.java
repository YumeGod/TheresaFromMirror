package cn.loli.client.gui.clickui.components;

import cn.loli.client.module.Module;
import dev.xix.property.AbstractTheresaProperty;

public class Component {
    public AbstractTheresaProperty value;
    public float x,y;

    public Component(AbstractTheresaProperty v) {
        this.value = v;
    }

    public void onMouse(int x, int y, int button) {

    }

    public void draw(float x, float y, float partialTicks) {

    }
}
