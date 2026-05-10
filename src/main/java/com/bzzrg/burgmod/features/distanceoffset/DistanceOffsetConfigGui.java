package com.bzzrg.burgmod.features.distanceoffset;

import com.bzzrg.burgmod.command.BMCommand;
import com.bzzrg.burgmod.modutils.gui.BMConfigGui;
import net.minecraft.client.Minecraft;

import static com.bzzrg.burgmod.config.files.mainconfigsections.DistanceOffsetConfig.*;

public class DistanceOffsetConfigGui extends BMConfigGui {

    public DistanceOffsetConfigGui() {

        this.setSettingWidth(150);

        this.addEnumSetting("Axis", Axis.class, () -> axis, v -> axis = v);
        this.addActionButton("How To Set LB/MM?", b -> {
            Minecraft.getMinecraft().displayGuiScreen(null);
            BMCommand.sendBMUsage();
        });

        this.nextColumn();
        this.addBooleanSetting("Title When +", () -> titleWhenPositive, v -> titleWhenPositive = v);
        this.addStringSetting("Text (& for formatting)", () -> titleTextPositive, v -> titleTextPositive = v, "Use & for formatting");
        this.addBooleanSetting("Sound When +", () -> soundWhenPositive, v -> soundWhenPositive = v);
        this.addFloatSetting("Sound Volume", () -> soundVolumePositive, v -> soundVolumePositive = v, 0, 1);
        this.nextColumn();
        this.addBooleanSetting("Title When Land", () -> titleWhenLand, v -> titleWhenLand = v);
        this.addStringSetting("Text (& for formatting)", () -> titleTextLand, v -> titleTextLand = v, "Use & for formatting");
        this.addBooleanSetting("Sound When Land", () -> soundWhenLand, v -> soundWhenLand = v);
        this.addFloatSetting("Sound Volume", () -> soundVolumeLand, v -> soundVolumeLand = v, 0, 1);
    }

}
