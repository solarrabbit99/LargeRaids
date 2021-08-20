package com.solarrabbit.largeraids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class VersionUtil {

    public static AbstractLargeRaid createLargeRaid(Location loc) {
        LargeRaids plugin = JavaPlugin.getPlugin(LargeRaids.class);
        if (getVersion().equals("v1_17_R1"))
            return new com.solarrabbit.largeraids.v1_17.LargeRaid(plugin, loc);
        return null;
    }

    public static AbstractRaiderConfig getRaiderConfig(EntityType type) {
        if (Bukkit.getVersion().equals("v1_17_R1"))
            return com.solarrabbit.largeraids.v1_17.RaiderConfig.valueOf(type);
        return null;
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
