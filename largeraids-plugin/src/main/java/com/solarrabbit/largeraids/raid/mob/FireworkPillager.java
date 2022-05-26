package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Pillager;

public class FireworkPillager implements Raider {
    private final Pillager bukkitEntity;

    public FireworkPillager(Pillager bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public Pillager getBukkitEntity() {
        return bukkitEntity;
    }

}
