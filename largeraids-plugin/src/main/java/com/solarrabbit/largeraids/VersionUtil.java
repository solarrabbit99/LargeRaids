package com.solarrabbit.largeraids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionUtil {

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

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
