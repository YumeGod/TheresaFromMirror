package dev.xix.gui.animation;

public enum AnimationEasing {
    EASE_IN {
        @Override
        public final float performAnimation(final float x) {
            return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
        }
    },
    EASE_OUT {
        @Override
        public final float performAnimation(final float x) {
            return (float) (x == 1 ? 1 : 1 - Math.pow(2, -10 * x));
        }
    },
    EASE_IN_OUT {
        @Override
        public final float performAnimation(final float x) {
            return x == 0 ? 0 : (float) (x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2);
        }
    };

    public abstract float performAnimation(final float x);
}
