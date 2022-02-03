package cn.loli.client.utils;

public class AnimationUtils {
    private TimerUtil timerUtil = new TimerUtil();


    public double animate(double target, double current, double speed, int delay) {
        if (timerUtil.delay(delay)) {
            boolean larger;
            boolean bl = larger = target > current;
            if (speed < 0.0) {
                speed = 0.0;
            } else if (speed > 1.0) {
                speed = 1.0;
            }
            double dif = Math.max(target, current) - Math.min(target, current);
            double factor = dif * speed;
            if (factor < 0.02) {
                factor = 0.02;
            }

            if (larger) {
                current += factor;
            } else {
                current -= factor;
            }
            timerUtil.reset();
        }
        if (Math.abs(current - target) <= 0.05)
            return target;
        else
            return current;
    }

    public float animate(float target, float current, float speed, int delay) {
        if (timerUtil.delay(delay)) {
            boolean larger;
            boolean bl = larger = target > current;
            if (speed < 0.0f) {
                speed = 0.0f;
            } else if (speed > 1.0) {
                speed = 1.0f;
            }
            float dif = Math.max(target, current) - Math.min(target, current);
            float factor = dif * speed;
            if (factor < 0.02) {
                factor = 0.02f;
            }

            if (larger) {
                current += factor;
            } else {
                current -= factor;
            }

            timerUtil.reset();
        }
        if (Math.abs(current - target) <= 0.05)
            return target;
        else
            return current;
    }

}
