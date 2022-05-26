package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.entity.LivingEntity;

public class MythicRaider implements Raider {
    private final LivingEntity bukkitEntity;

    public MythicRaider(LivingEntity bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public LivingEntity getBukkitEntity() {
        return bukkitEntity;
    }

}
