package com.bzzrg.burgmod.features.general;

import com.bzzrg.burgmod.features.BMConfigGui;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.config.basicconfig.GeneralConfig.*;
import static com.bzzrg.burgmod.utils.GeneralUtils.*;

public class GeneralConfigGui extends BMConfigGui {
    public GeneralConfigGui() {

        final List<EnumChatFormatting> colors = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toList());
        this.addStringSetting("Color 1", () -> color1, s -> {
            try {
                color1 = EnumChatFormatting.valueOf(s.toUpperCase()).toString();
                bmChat("\u00A7aColor 1 has been set to: " + color1 + s.toUpperCase());
            } catch (Exception e) {
                bmChat("\u00A7cInput a valid MC Color Code (caps don't matter):");
                chat(colors.stream().map(c -> c + c.name()).collect(Collectors.joining(", ")));
                playErrorSound();
            }
        }, "MC Color Code");
        this.addStringSetting("Color 2", () -> color2, s -> {
            try {
                color2 = EnumChatFormatting.valueOf(s.toUpperCase()).toString();
                bmChat("\u00A7aColor 2 has been set to: " + color2 + s.toUpperCase());
            } catch (Exception e) {
                bmChat("\u00A7cInput a valid MC Color Code (caps don't matter):");
                chat(colors.stream().map(c -> c + c.name()).collect(Collectors.joining(", ")));
                playErrorSound();
            }
        }, "MC Color Code");
        this.addIntSetting("Decimal Precision", () -> decimalPrecision, i -> decimalPrecision = i, 0, 100);
    }
}
