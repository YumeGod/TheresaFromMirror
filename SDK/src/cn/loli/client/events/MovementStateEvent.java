package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class MovementStateEvent implements Event {
    float moveForward, moveStrafe, yaw, shouldYaw;
    boolean silentMoveFix, fixYaw, sneak;

    public MovementStateEvent(float moveForward, float moveStrafe, float yaw, float shouldYaw, boolean silentMoveFix, boolean fixYaw, boolean sneak) {
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
        this.yaw = yaw;
        this.shouldYaw = shouldYaw;
        this.silentMoveFix = silentMoveFix;
        this.fixYaw = fixYaw;
        this.sneak = sneak;
    }

    public float getMoveForward() {
        return this.moveForward;
    }

    public float getMoveStrafe() {
        return this.moveStrafe;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getShouldYaw() {
        return this.shouldYaw;
    }

    public boolean isSilentMoveFix() {
        return this.silentMoveFix;
    }

    public boolean isFixYaw() {
        return this.fixYaw;
    }

    public boolean isSneak() {
        return this.sneak;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setShouldYaw(float shouldYaw) {
        this.shouldYaw = shouldYaw;
    }

    public void setSilentMoveFix(boolean silentMoveFix) {
        this.silentMoveFix = silentMoveFix;
    }

    public void setFixYaw(boolean fixYaw) {
        this.fixYaw = fixYaw;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

}
