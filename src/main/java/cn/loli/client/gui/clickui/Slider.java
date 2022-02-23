package cn.loli.client.gui.clickui;

import cn.loli.client.utils.render.AnimationUtils;

public class Slider {
    public float top, top1;

    float s1;

    public void update() {
        top = AnimationUtils.smoothAnimation(top, top1, 50f, s1);
    }

    public void change(float newTop) {
        s1 = 0.3f;
        this.top1 = newTop;
    }

}
