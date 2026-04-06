package com.bzzrg.burgmod.features.turnhelper;

public enum Mode {
    ONE_MOVING_TARGET, ALL_TARGETS_ON;
    @Override
    public String toString() {
        switch (this) {
            case ONE_MOVING_TARGET: return "One Moving Target";
            case ALL_TARGETS_ON: return "All Targets On";
            default: return this.name();
        }
    }
}
