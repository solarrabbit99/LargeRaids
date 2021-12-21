package com.solarrabbit.largeraids.v1_14.nms;

import java.util.Set;
import java.util.UUID;

import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.server.v1_14_R1.Raid;

public class RaidWrapper implements AbstractRaidWrapper {
    final Raid raid;

    RaidWrapper(Raid raid) {
        this.raid = raid;
    }

    @Override
    public boolean isEmpty() {
        return this.raid == null;
    }

    @Override
    public void stop() {
        this.raid.n();
    }

    @Override
    public void setBadOmenLevel(int level) {
        this.raid.o = level;
    }

    @Override
    public int getGroupsSpawned() {
        return this.raid.k();
    }

    @Override
    public void setGroupsSpawned(int groupsSpawned) {
        try {
            FieldUtils.writeField(this.raid, "q", groupsSpawned, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addWaveMob(int wave, AbstractRaiderWrapper raider, boolean flag) {
        return this.raid.a(wave, ((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public void removeFromRaid(AbstractRaiderWrapper raider, boolean flag) {
        this.raid.a(((RaiderWrapper) raider).raider, flag);
    }

    @Override
    public Set<UUID> getHeroesOfTheVillage() {
        return this.raid.h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RaidWrapper wrapper) {
            return this.raid == wrapper.raid;
        } else {
            return false;
        }
    }
}
