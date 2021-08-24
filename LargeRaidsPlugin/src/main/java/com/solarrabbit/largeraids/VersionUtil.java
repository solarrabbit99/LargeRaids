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
        if (getVersion().equals("v1_16_R3"))
            return new com.solarrabbit.largeraids.v1_16.LargeRaid(plugin, loc);
        if (getVersion().equals("v1_15_R1"))
            return new com.solarrabbit.largeraids.v1_15.LargeRaid(plugin, loc);
        if (getVersion().equals("v1_14_R1"))
            return new com.solarrabbit.largeraids.v1_14.LargeRaid(plugin, loc);
        return null;
    }

    public static AbstractRaiderConfig getRaiderConfig(EntityType type) {
        if (getVersion().equals("v1_17_R1"))
            return com.solarrabbit.largeraids.v1_17.RaiderConfig.valueOf(type);
        if (getVersion().equals("v1_16_R3"))
            return com.solarrabbit.largeraids.v1_16.RaiderConfig.valueOf(type);
        if (getVersion().equals("v1_15_R1"))
            return com.solarrabbit.largeraids.v1_15.RaiderConfig.valueOf(type);
        if (getVersion().equals("v1_14_R1"))
            return com.solarrabbit.largeraids.v1_14.RaiderConfig.valueOf(type);
        return null;
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
