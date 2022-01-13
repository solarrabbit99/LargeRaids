package com.solarrabbit.largeraids;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.plugin.java.JavaPlugin;

public class ResourceChecker {
    private static final String UPDATE_BASE_URL = "https://api.spigotmc.org/legacy/update.php?resource=";

    public CompletableFuture<Boolean> hasUpdate(JavaPlugin plugin, int resourceId) {
        final String pluginVersion = plugin.getDescription().getVersion();
        return CompletableFuture.supplyAsync(() -> getVersion(resourceId))
                .thenApply(update -> VersionUtil.compare(update, pluginVersion) > 0);
    }

    private String getVersion(int resourceId) {
        try {
            URL url = new URL(UPDATE_BASE_URL + resourceId);
            Scanner scanner = new Scanner(url.openStream());
            if (!scanner.hasNextLine())
                return null;
            String version = scanner.nextLine();
            scanner.close();
            return version;
        } catch (IOException e) {
            return null;
        }
    }

}
