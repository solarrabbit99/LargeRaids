package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Raider;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;

public class EventMythicRaider implements EventRaider {
    private final MythicMob type;

    public EventMythicRaider(MythicMob type) {
        this.type = type;
    }

    @Override
    public Raider spawn(Location location) {
        ActiveMob activeMob = type.spawn(BukkitAdapter.adapt(location), 1);
        Entity bukkitEntity = activeMob.getEntity().getBukkitEntity();
        if (!(bukkitEntity instanceof Raider)) {
            bukkitEntity.remove();
            throw new MythicMobNotRaiderException();
        }
        return (Raider) bukkitEntity;
    }

    private class MythicMobNotRaiderException extends RuntimeException {

        private MythicMobNotRaiderException() {
            super(type + " is not a type of Raider!");
        }

    }

}
