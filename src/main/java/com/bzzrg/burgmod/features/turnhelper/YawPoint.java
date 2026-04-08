package com.bzzrg.burgmod.features.turnhelper;

public class YawPoint {
    public int tickNum;
    public float yaw;
    public TurnHelperListGui.YawPointRow row = null;

    public YawPoint(int tickNum, float yaw) {
        this.tickNum = tickNum;
        this.yaw = yaw;
    }
}
