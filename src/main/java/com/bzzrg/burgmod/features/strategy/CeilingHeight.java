package com.bzzrg.burgmod.features.strategy;

public enum CeilingHeight {
    BC_2, BC_2_5, BC_3, NO_CEIL;
    @Override
    public String toString() {
        switch (this) {
            case BC_2: return "2bc";
            case BC_2_5: return "2.5bc";
            case BC_3: return "3bc";
            case NO_CEIL: return "No Ceil";
            default: return this.name();
        }
    }

    public int getJumpLength() {
        switch (this) {
            case BC_3: return 11;
            case BC_2_5: return 6;
            case BC_2: return 3;
            default: return 12;
        }
    }
}
