package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.raid.mob.Raider;

import org.bukkit.Location;

public interface CustomRaiderManager extends MobManager {
    Raider spawn(Location location);
}
