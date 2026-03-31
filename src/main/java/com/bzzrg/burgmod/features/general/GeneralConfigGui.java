package com.bzzrg.burgmod.features.general;

import com.bzzrg.burgmod.modutils.gui.BMConfigGui;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.bzzrg.burgmod.config.files.mainconfigsections.GeneralConfig.*;
import static com.bzzrg.burgmod.modutils.GeneralUtils.*;

public class GeneralConfigGui extends BMConfigGui {

    private static final List<EnumChatFormatting> colors = Arrays.stream(EnumChatFormatting.values()).filter(EnumChatFormatting::isColor).collect(Collectors.toList());

    public GeneralConfigGui() {

        this.addStringSetting("Color 1", () -> charToName(color1), s -> {

            try {
                color1 = EnumChatFormatting.valueOf(s.toUpperCase()).toString();
                bmChat("\u00A7aColor 1 has been set to: " + color1 + s.toUpperCase());
            } catch (Exception e) {
                sendError();
            }

        }, "MC Color Code");
        this.addStringSetting("Color 2", () -> charToName(color2), s -> {

            try {
                color2 = EnumChatFormatting.valueOf(s.toUpperCase()).toString();
                bmChat("\u00A7aColor 2 has been set to: " + color2 + s.toUpperCase());
            } catch (Exception e) {
                sendError();
            }

        }, "MC Color Code");

        this.addIntSetting("Decimal Precision", () -> decimalPrecision, i -> decimalPrecision = i, 0, 100);
    }

    private static String charToName(String color) {

        for (EnumChatFormatting f : colors) {
            if (f.toString().equals(color)) return f.getFriendlyName();
        }

        return color;
    }
    private static void sendError() {
        bmChat("\u00A7cInvalid MC Color Code! List of valid MC Color Codes (caps don't matter):");
        chat(colors.stream().map(c -> c + c.getFriendlyName()).collect(Collectors.joining(", ")));
        playErrorSound();
    }

}
