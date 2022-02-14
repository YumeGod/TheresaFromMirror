package cn.loli.client.gui.clickui;

import cn.loli.client.utils.AnimationUtils;

public class Slider {
    public float top, top1;

    AnimationUtils a1 = new AnimationUtils();
    float s1;

    public void update() {
        top = a1.animate(top1, top, s1, 30);
    }

    public void change(float newTop) {
        s1 = 0.3f;
        this.top1 = newTop;
    }

}
