package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.raid.mob.VanillaRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRiderRaider;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;

public class VanillaRaiderManager implements MobManager {

    public VanillaRaider spawn(Location location, EntityType type) {
        Raider raider = (Raider) location.getWorld().spawnEntity(location, type);
        return new VanillaRaider(raider);
    }

    public VanillaRiderRaider spawnRider(Location location, EntityType type) {
        Raider rider = (Raider) location.getWorld().spawnEntity(location, type);
        Ravager ravager = (Ravager) location.getWorld().spawnEntity(location, EntityType.RAVAGER);
        ravager.addPassenger(rider);
        return new VanillaRiderRaider(rider, ravager);
    }

}
