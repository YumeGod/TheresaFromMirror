package dev.xix.util.system;

public final class Stopwatch {
    private double currentMilliseconds;

    public Stopwatch() {
        this.reset();
    }

    public long getLastReset() {
        return (long) this.currentMilliseconds;
    }

    public boolean hasElapsed(final double time) {
        return elapsedTime() > time;
    }

    public long elapsedTime() {
        return (long) (System.currentTimeMillis() - currentMilliseconds);
    }

    public void reset() {
        this.currentMilliseconds = System.currentTimeMillis();
    }

}
