package com.solarrabbit.largeraids.nms;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

public interface AbstractRaidWrapper {
    /**
     * Returns if the wrapped NMS raid is {@code null}.
     *
     * @return {@code true} if NMS raid is {@code null}
     */
    boolean isEmpty();

    void stop();

    boolean isBetweenWaves();

    boolean hasFirstWaveSpawned();

    void setBadOmenLevel(int level);

    int getGroupsSpawned();

    void setGroupsSpawned(int groupsSpawned);

    void joinRaid(int i, AbstractRaiderWrapper raider, @Nullable AbstractBlockPositionWrapper blockPosition,
            boolean flag);

    boolean addWaveMob(int wave, AbstractRaiderWrapper raider, boolean flag);

    void removeFromRaid(AbstractRaiderWrapper raider, boolean flag);

    Set<UUID> getHeroesOfTheVillage();

    boolean isActive();
}
