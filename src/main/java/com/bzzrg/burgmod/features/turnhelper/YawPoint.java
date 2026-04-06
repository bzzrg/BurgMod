package com.bzzrg.burgmod.features.turnhelper;

public class YawPoint {
    public float yaw;
    public int tickNum;
    public TurnHelperListGui.YawPointRow row = null;

    public YawPoint(float yaw, int tickNum) {
        this.yaw = yaw;
        this.tickNum = tickNum;
    }
}
