package com.solarrabbit.largeraids.v1_18_R2.nms;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.world.entity.raid.Raid;

public class RaidWrapper implements AbstractRaidWrapper {
    final Raid raid;

    RaidWrapper(Raid raid) {
        this.raid = raid;
    }

    @Override
    public boolean isEmpty() {
        return raid == null;
    }

    @Override
    public void stop() {
        raid.n();
    }

    @Override
    public boolean isBetweenWaves() {
        return raid.b();
    }

    @Override
    public boolean hasFirstWaveSpawned() {
        return raid.c();
    }

    @Override
    public void setBadOmenLevel(int level) {
        this.raid.a(level);
    }

    @Override
    public int getGroupsSpawned() {
        return this.raid.k();
    }

    @Override
    public void setGroupsSpawned(int groupsSpawned) {
        try {
            FieldUtils.writeField(this.raid, "L", groupsSpawned, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinRaid(int i, AbstractRaiderWrapper raider, @Nullable AbstractBlockPositionWrapper blockPosition,
            boolean flag) {
        raid.a(i, ((RaiderWrapper) raider).raider,
                blockPosition == null ? null : ((BlockPositionWrapper) blockPosition).blockPos, flag);
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
        return this.raid.C;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RaidWrapper) {
            return this.raid == ((RaidWrapper) obj).raid;
        } else {
            return false;
        }
    }

    @Override
    public boolean isActive() {
        return raid.v();
    }
}
