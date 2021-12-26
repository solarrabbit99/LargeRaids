package com.solarrabbit.largeraids.v1_15.nms;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;

import net.minecraft.server.v1_15_R1.Raid;

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
        this.raid.stop();
    }

    @Override
    public void setBadOmenLevel(int level) {
        this.raid.badOmenLevel = level;
    }

    @Override
    public int getGroupsSpawned() {
        return this.raid.getGroupsSpawned();
    }

    @Override
    public void setGroupsSpawned(int groupsSpawned) {
        try {
            FieldUtils.writeField(this.raid, "groupsSpawned", groupsSpawned, true);
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
        return this.raid.heroes;
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
