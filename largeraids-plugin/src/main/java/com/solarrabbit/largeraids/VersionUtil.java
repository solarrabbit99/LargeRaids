package com.solarrabbit.largeraids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionUtil {
    private static final String[] VERSIONS = new String[] { "v1_14_R1", "v1_15_R1", "v1_16_R3", "v1_17_R1" };

    public static AbstractLargeRaid createLargeRaid(Location loc, Player player) {
        LargeRaids plugin = JavaPlugin.getPlugin(LargeRaids.class);
        if (getVersion().equals("v1_17_R1"))
            return new com.solarrabbit.largeraids.v1_17.LargeRaid(plugin, loc, player);
        if (getVersion().equals("v1_16_R3"))
            return new com.solarrabbit.largeraids.v1_16.LargeRaid(plugin, loc, player);
        if (getVersion().equals("v1_15_R1"))
            return new com.solarrabbit.largeraids.v1_15.LargeRaid(plugin, loc, player);
        if (getVersion().equals("v1_14_R1"))
            return new com.solarrabbit.largeraids.v1_14.LargeRaid(plugin, loc, player);
        return null;
    }

    public static AbstractVillages getVillageManager() {
        if (getVersion().equals("v1_17_R1"))
            return new com.solarrabbit.largeraids.v1_17.CustomVillages();
        if (getVersion().equals("v1_16_R3"))
            return new com.solarrabbit.largeraids.v1_16.CustomVillages();
        if (getVersion().equals("v1_15_R1"))
            return new com.solarrabbit.largeraids.v1_15.CustomVillages();
        if (getVersion().equals("v1_14_R1"))
            return new com.solarrabbit.largeraids.v1_14.CustomVillages();
        return null;
    }

    public static boolean isAtLeast(String version) {
        boolean hasMet = false;
        String currentVersion = getVersion();
        for (int i = 0; i < VERSIONS.length; i++) {
            if (!hasMet && VERSIONS[i].equals(version))
                hasMet = true;
            if (VERSIONS[i].equals(currentVersion))
                return hasMet;
        }
        return false;
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
