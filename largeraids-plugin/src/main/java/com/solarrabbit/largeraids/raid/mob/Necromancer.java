package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.Spellcaster;

public class Necromancer implements Raider {
    private final Spellcaster bukkitEntity;

    public Necromancer(Spellcaster bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public Spellcaster getBukkitEntity() {
        return bukkitEntity;
    }

}
