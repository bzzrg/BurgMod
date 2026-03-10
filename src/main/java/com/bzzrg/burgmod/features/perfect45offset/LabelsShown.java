package com.bzzrg.burgmod.features.perfect45offset;

public enum LabelsShown {
    AUTO, XZ;
    @Override
    public String toString() {
        switch (this) {
            case AUTO: return "Auto";
            case XZ: return "X & Z";
            default: return name();
        }
    }
}
