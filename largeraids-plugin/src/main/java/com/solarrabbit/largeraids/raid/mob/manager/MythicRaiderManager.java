package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.raid.mob.MythicRaider;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;

public class MythicRaiderManager implements MobManager {

    public MythicRaider spawn(Location location, MythicMob type) {
        ActiveMob activeMob = type.spawn(BukkitAdapter.adapt(location), 1);
        Entity bukkitEntity = activeMob.getEntity().getBukkitEntity();
        if (!(bukkitEntity instanceof Raider)) {
            bukkitEntity.remove();
            throw new MythicMobNotRaiderException(type);
        }
        return new MythicRaider((LivingEntity) bukkitEntity);
    }

    private class MythicMobNotRaiderException extends RuntimeException {

        private MythicMobNotRaiderException(MythicMob type) {
            super(type + " is not a type of Raider!");
        }

    }
}
