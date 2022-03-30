package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class CameraEvent implements Event {

    public double posX, posY, posZ, prevPosX, prevPosY, prevPosZ;
    public float yaw, pitch, prevYaw, prevPitch;

    public CameraEvent(double posX, double posY, double posZ, double prevPosX, double prevPosY, double prevPosZ, float yaw, float pitch, float prevYaw, float prevPitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.prevPosX = prevPosX;
        this.prevPosY = prevPosY;
        this.prevPosZ = prevPosZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
    }


    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }


    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setPrevYaw(float prevYaw) {
        this.prevYaw = prevYaw;
    }

    public void setPrevPitch(float prevPitch) {
        this.prevPitch = prevPitch;
    }

    public float getPrevYaw() {
        return prevYaw;
    }

    public float getPrevPitch() {
        return prevPitch;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public double getPrevPosX() {
        return prevPosX;
    }

    public double getPrevPosY() {
        return prevPosY;
    }

    public double getPrevPosZ() {
        return prevPosZ;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public void setPrevPosX(double prevPosX) {
        this.prevPosX = prevPosX;
    }

    public void setPrevPosY(double prevPosY) {
        this.prevPosY = prevPosY;
    }

    public void setPrevPosZ(double prevPosZ) {
        this.prevPosZ = prevPosZ;
    }

}
