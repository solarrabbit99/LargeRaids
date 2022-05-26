package com.solarrabbit.largeraids.raid.mob;

public class VanillaRaider implements Raider {
    private final org.bukkit.entity.Raider bukkitEntity;

    public VanillaRaider(org.bukkit.entity.Raider bukkitEntity) {
        this.bukkitEntity = bukkitEntity;
    }

    @Override
    public org.bukkit.entity.Raider getBukkitEntity() {
        return bukkitEntity;
    }

}
