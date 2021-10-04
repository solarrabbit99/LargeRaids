package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.Location;
import org.bukkit.entity.Raider;

public interface EventRaider extends AbstractRaider {

    Raider spawn(Location location);

}
