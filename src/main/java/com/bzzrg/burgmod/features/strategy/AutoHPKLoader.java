package com.bzzrg.burgmod.features.strategy;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.files.jsonconfigfiles.StrategyConfig;
import com.bzzrg.burgmod.config.files.utils.JsonConfigFile;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.modutils.GeneralUtils.createDirectory;

public class AutoHPKLoader {

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {

        String msg = event.message.getUnformattedText();

        if (StrategyConfig.autoLoadHPK && msg.startsWith("[OJ] Entering Jump ")) {

            File hpkStrategies = new File(BurgMod.modConfigFolder, "strategy/saved-hpk-strategies");
            createDirectory(hpkStrategies);
            File[] hpkStrategiesList = hpkStrategies.listFiles();
            if (hpkStrategiesList == null) return;

            String jumpNum = msg.replace("[OJ] Entering Jump ", "").replace("...", "");



            for (File hpkStratFile : hpkStrategiesList) {

                String trimmedName = hpkStratFile.getName().replace(".json", "");

                if (jumpNum.equals(trimmedName)) {

                    new JsonConfigFile(hpkStratFile) {
                        @Override
                        protected void init() {
                            addJson("strategy", StrategyConfig.convertor);
                        }
                    }.updateFields();

                    StrategyConfig.instance.updateFile();

                    bmChat("\u00A7aAutomatically loaded HPK strategy under jump #: \u00A7b" + trimmedName);
                    return;
                }
            }
        }
    }
}