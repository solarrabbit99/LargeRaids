package com.solarrabbit.largeraids.village;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public interface AbstractVillages {

    boolean addVillage(Location location);

    void removeVillage(Location location);

    default JavaPlugin getPlugin() {
        return null;
    }

}
