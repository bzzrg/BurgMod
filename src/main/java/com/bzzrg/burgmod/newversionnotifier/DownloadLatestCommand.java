package com.bzzrg.burgmod.newversionnotifier;

import com.bzzrg.burgmod.BurgMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.bzzrg.burgmod.BurgMod.mc;
import static com.bzzrg.burgmod.modutils.GeneralUtils.bmChat;
import static com.bzzrg.burgmod.newversionnotifier.NewVersionNotifier.cachedDownloadUrl;

public class DownloadLatestCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bmdownloadlatest";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return null;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        new Thread(() -> {
            try {
                mc.addScheduledTask(() -> bmChat("\u00A7eDownloading latest version of BurgMod..."));

                // 1. Download the new version directly to mods folder (versioned filename)
                String fileName = cachedDownloadUrl.substring(cachedDownloadUrl.lastIndexOf('/') + 1);
                File newJar = new File("mods", fileName);

                try (InputStream in = new URL(cachedDownloadUrl).openStream()) {
                    Files.copy(in, newJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                // 2. Verify the current mod jar is valid (the one that is running now)
                if (BurgMod.currentModJar == null || !BurgMod.currentModJar.exists() || !BurgMod.currentModJar.getName().endsWith(".jar")) {
                    mc.addScheduledTask(() -> bmChat("\u00A7cUpdate failed: Cannot locate current mod jar"));
                    newJar.delete(); // remove the downloaded file
                    return;
                }

                // 3. Schedule deletion of the old jar on JVM shutdown (hidden batch)
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        File batch = new File(System.getProperty("java.io.tmpdir"), "delete_burgmod.bat");
                        String content = "@echo off\r\n" +
                                "ping -n 6 127.0.0.1 > nul\r\n" +
                                "takeown /f \"" + BurgMod.currentModJar.getAbsolutePath() + "\" > nul 2>&1\r\n" +
                                "icacls \"" + BurgMod.currentModJar.getAbsolutePath() + "\" /grant %username%:F > nul 2>&1\r\n" +
                                "del /f /q \"" + BurgMod.currentModJar.getAbsolutePath() + "\"\r\n" +
                                "del /f /q \"%~f0\"\r\n";
                        Files.write(batch.toPath(), content.getBytes());
                        // Hidden execution – no window popup
                        Runtime.getRuntime().exec("cmd.exe /c start /b \"\" \"" + batch.getAbsolutePath() + "\"");
                    } catch (Exception e) {
                        System.out.println("[BurgMod] Shutdown hook error: " + e.getMessage());
                    }
                }));

                mc.addScheduledTask(() -> bmChat("\u00A7aDownload complete! Restart your game to use the new version."));

            } catch (Exception e) {
                // If anything fails, the old jar is never scheduled for deletion,
                // and any partially downloaded new jar is already cleaned up (or we clean it here)
                mc.addScheduledTask(() -> bmChat("\u00A7cUpdate failed: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }
}
