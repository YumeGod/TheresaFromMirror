package cn.loli.client.events;

import dev.xix.event.Event;

public class Render3DEvent extends Event {
    int pass;
    float partialTicks;
    long finishTimeNano;

    public Render3DEvent(int pass, float partialTicks, long finishTimeNano) {
        this.pass = pass;
        this.partialTicks = partialTicks;
        this.finishTimeNano = finishTimeNano;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public long getFinishTimeNano() {
        return finishTimeNano;
    }

    public void setFinishTimeNano(long finishTimeNano) {
        this.finishTimeNano = finishTimeNano;
    }
}
