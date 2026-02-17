package com.bzzrg.burgmod.features.trajectory;

import com.bzzrg.burgmod.config.featureconfig.TrajectoryConfig;
import com.bzzrg.burgmod.features.FeatureConfigGui;

public class TrajectoryConfigGui extends FeatureConfigGui {
    public TrajectoryConfigGui() {
        this.addFloatSetting("Red", () -> TrajectoryConfig.colorRed, f -> TrajectoryConfig.colorRed = f, 0, 1);
        this.addFloatSetting("Green", () -> TrajectoryConfig.colorGreen, f -> TrajectoryConfig.colorGreen = f, 0, 1);
        this.addFloatSetting("Blue", () -> TrajectoryConfig.colorBlue, f -> TrajectoryConfig.colorBlue = f, 0, 1);
        this.addFloatSetting("Alpha", () -> TrajectoryConfig.alpha, f -> TrajectoryConfig.alpha = f, 0, 1);
        this.addFloatSetting("Thickness", () -> TrajectoryConfig.thickness, f -> TrajectoryConfig.thickness = f, 0, 0.2f);
        this.addIntSetting("Tick Length", () -> TrajectoryConfig.tickLength, i -> TrajectoryConfig.tickLength = i, 0, 200);
    }

}
