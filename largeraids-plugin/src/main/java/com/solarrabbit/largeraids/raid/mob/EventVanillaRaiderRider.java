package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;

public class EventVanillaRaiderRider implements EventRaider {
    private final EntityType type;
    private Raider rider;

    public EventVanillaRaiderRider(EntityType type) {
        this.type = type;
    }

    @Override
    public Raider spawn(Location location) {
        rider = (Raider) location.getWorld().spawnEntity(location, type);
        Ravager ravager = (Ravager) location.getWorld().spawnEntity(location, EntityType.RAVAGER);
        ravager.addPassenger(rider);
        return ravager;
    }

    public Raider getRider() {
        return rider;
    }

}
