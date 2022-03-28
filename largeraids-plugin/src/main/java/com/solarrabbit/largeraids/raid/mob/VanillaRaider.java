package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;

public class VanillaRaider implements EventRaider {
    private final EntityType type;

    public VanillaRaider(EntityType type) {
        this.type = type;
    }

    @Override
    public Raider spawn(Location location) {
        return (Raider) location.getWorld().spawnEntity(location, type);
    }

}
