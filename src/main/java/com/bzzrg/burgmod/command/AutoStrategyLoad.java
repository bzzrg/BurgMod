package com.bzzrg.burgmod.command;

import com.bzzrg.burgmod.BurgMod;
import com.bzzrg.burgmod.config.ConfigHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static com.bzzrg.burgmod.config.featureconfig.StrategyConfig.updateStrategyFields;
import static com.bzzrg.burgmod.utils.PluginUtils.createDirectory;
import static com.bzzrg.burgmod.utils.PluginUtils.sendMessage;

public class AutoStrategyLoad {

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {

        String msg = event.message.getUnformattedText();

        if (ConfigHandler.autoStrategyLoadOn && msg.startsWith("[OJ] Entering Jump ")) {

            File hpkStrategies = new File(BurgMod.modConfigFolder, "input_status/hpk_strategies");
            createDirectory(hpkStrategies);

            String jumpNum = msg.replace("[OJ] Entering Jump ", "").replace("...", "");

            for (File hpkStrategy : Objects.requireNonNull(hpkStrategies.listFiles())) { // In practice, .listFiles will never produce an NPE, requireNonNull is to silence IDE
                String trimmedName = hpkStrategy.getName().replace(".json", "");
                if (jumpNum.equals(trimmedName)) {
                    try {
                        Files.copy(hpkStrategy.toPath(), new File(BurgMod.modConfigFolder, "input_status/strategy.json").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        BurgMod.logger.error("Failed to load " + hpkStrategy.getName() + ".json into strategy.json", e);
                        return;
                    }

                    updateStrategyFields();
                    sendMessage("\u00A7aAutomatically loaded HPK strategy under jump #: \u00A7b" + trimmedName);
                    return;
                }

            }

        }
    }
}
