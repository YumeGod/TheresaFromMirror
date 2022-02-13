package cn.loli.client.gui.clickui;

import cn.loli.client.utils.AnimationUtils;

public class Slider {
    public float top, bottom;
    public float top1, bottom1;

    AnimationUtils a1 = new AnimationUtils();
    AnimationUtils a2 = new AnimationUtils();
    float s1, s2;

    public void update() {
        top = a1.animate(top1, top, s1, 30);
        bottom = a2.animate(bottom1, bottom, s2, 30);
    }

    public void change(float newTop, float newBottom) {
        if (newTop >= top1) {
            s2 = 0.3f;
            s1 = 0.15f;
        } else {
            s2 = 0.15f;
            s1 = 0.3f;
        }
        this.top1 = newTop;
        this.bottom1 = newBottom;
    }

}
