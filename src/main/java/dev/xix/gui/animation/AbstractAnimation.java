package dev.xix.gui.animation;

import dev.xix.gui.element.AbstractElement;
import dev.xix.util.system.Stopwatch;
import scala.reflect.api.Trees;

public abstract class AbstractAnimation {
    protected final AnimationEasing easing;
    protected AbstractElement element;
    protected AbstractElement startElement;

    private final long duration;
    private final Stopwatch stopwatch;

    public AbstractAnimation(final long duration, final AnimationEasing easing) {
        this.duration = duration;
        this.easing = easing;

        this.stopwatch = new Stopwatch();
    }

    protected final float getCompletion() {
        float completion = Math.min(stopwatch.elapsedTime() / duration, 1);
        if (easing != null)
            completion = easing.performAnimation(completion);
        return completion;
    }

    public abstract boolean process();

    public void init(final AbstractElement element) {
        this.startElement = element.clone();
        this.element = element;
    }

    public long getDuration() {
        return duration;
    }

    public Stopwatch getStopwatch() {
        return stopwatch;
    }
}
