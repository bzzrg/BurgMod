package com.bzzrg.burgmod.features.turnhelper;

public enum Shape {
    LINE, DOT;
    @Override
    public String toString() {
        switch (this) {
            case LINE: return "Line";
            case DOT: return "Dot";
            default: return this.name();
        }
    }
}
