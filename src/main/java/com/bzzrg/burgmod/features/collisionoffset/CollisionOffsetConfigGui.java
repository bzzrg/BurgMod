package com.bzzrg.burgmod.features.collisionoffset;

import com.bzzrg.burgmod.command.BMCommand;
import com.bzzrg.burgmod.modutils.gui.BMConfigGui;
import net.minecraft.client.Minecraft;

import static com.bzzrg.burgmod.config.files.mainconfigsections.CollisionOffsetConfig.*;

public class CollisionOffsetConfigGui extends BMConfigGui {

    public CollisionOffsetConfigGui() {

        this.setSettingWidth(150);

        this.addBooleanSetting("Show X Label", () -> showXLabel, v -> showXLabel = v);
        this.addBooleanSetting("Show Z Label", () -> showZLabel, v -> showZLabel = v);
        this.addActionButton("How To Set Col Blocks?", b -> {
            Minecraft.getMinecraft().displayGuiScreen(null);
            BMCommand.sendBMUsage();
        });
    }

}
