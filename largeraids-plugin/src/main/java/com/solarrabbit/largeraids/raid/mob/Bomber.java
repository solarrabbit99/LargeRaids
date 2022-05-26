package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Spellcaster;

public class Bomber implements Raider {
    private final Spellcaster bukkitEntity;

    public Bomber(Spellcaster bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public Spellcaster getBukkitEntity() {
        return bukkitEntity;
    }

}
