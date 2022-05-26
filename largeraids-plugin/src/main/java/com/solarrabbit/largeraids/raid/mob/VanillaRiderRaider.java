package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;

public class VanillaRiderRaider implements RiderRaider {
    private final Raider rider;
    private final Ravager ravager;

    public VanillaRiderRaider(Raider rider, Ravager ravager) {
        this.rider = rider;
        this.ravager = ravager;
    }

    @Override
    public Raider getBukkitEntity() {
        return rider;
    }

    @Override
    public Ravager getVehicle() {
        return ravager;
    }

}
