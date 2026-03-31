package com.bzzrg.burgmod.modutils.simulation;

public class UpdateSimOptions {
    public final Boolean W, A, S, D, JUMP, SPR, SNK;
    public final Float rotationYaw;

    public UpdateSimOptions(Boolean W, Boolean A, Boolean S, Boolean D, Boolean SPR, Boolean SNK, Boolean JUMP, Float rotationYaw) {
        this.W = W;
        this.A = A;
        this.S = S;
        this.D = D;
        this.SPR = SPR;
        this.SNK = SNK;
        this.JUMP = JUMP;
        this.rotationYaw = rotationYaw;
    }
}