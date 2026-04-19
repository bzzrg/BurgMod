package com.bzzrg.burgmod.features.distance;

import com.bzzrg.burgmod.modutils.gui.BMConfigGui;

import static com.bzzrg.burgmod.config.files.mainconfigsections.DistanceOffsetConfig.*;

public class DistanceOffsetConfigGui extends BMConfigGui {

    public DistanceOffsetConfigGui() {
        this.addEnumSetting("Axis", Axis.class, () -> axis, v -> axis = v);
        this.addStringSetting("Title When +", () -> titleWhenPositive, v -> titleWhenPositive = v, "Use & for formatting");
        this.addBooleanSetting("Sound When +", () -> soundWhenPositive, v -> soundWhenPositive = v);
        this.addFloatSetting("Volume Of Sound", () -> volumeOfSound, v -> volumeOfSound = v, 0, 1);
    }

}
