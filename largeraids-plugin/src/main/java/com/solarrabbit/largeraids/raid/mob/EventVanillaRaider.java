package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;

public class EventVanillaRaider implements EventRaider {
    private final EntityType type;

    public EventVanillaRaider(EntityType type) {
        this.type = type;
    }

    @Override
    public boolean canGiveOmen() {
        return false;
    }

    @Override
    public Raider spawn(Location location) {
        return (Raider) location.getWorld().spawnEntity(location, type);
    }

}
