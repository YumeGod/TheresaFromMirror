

package cn.loli.client.utils;

public class TimeHelper {
    private long lastMS;

    public TimeHelper() {
        this.lastMS = 0L;
    }

    public boolean delay(float milliSec) {
        return (float) (getCurrentMS() - this.lastMS) >= milliSec;
    }

    public int convertToMS(int d) {
        return 1000 / d;
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public boolean hasTimeReached(long delay) {
        return System.currentTimeMillis() - lastMS >= delay;
    }

    public long getDelay() {
        return System.currentTimeMillis() - lastMS;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

    public void setLastMS() {
        lastMS = System.currentTimeMillis();
    }

    public void setLastMS(long lastMS) {
        this.lastMS = lastMS;
    }

    public long getDifference() {
        return getCurrentMS() - lastMS;
    }

    public void setDifference(long diff) {
        lastMS = (getCurrentMS() - diff);
    }
}