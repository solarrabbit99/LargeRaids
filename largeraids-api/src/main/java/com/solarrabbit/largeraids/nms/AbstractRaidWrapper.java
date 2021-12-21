package com.solarrabbit.largeraids.nms;

import java.util.Set;
import java.util.UUID;

public interface AbstractRaidWrapper {
    boolean isEmpty();

    void stop();

    void setBadOmenLevel(int level);

    int getGroupsSpawned();

    void setGroupsSpawned(int groupsSpawned);

    boolean addWaveMob(int wave, AbstractRaiderWrapper raider, boolean flag);

    void removeFromRaid(AbstractRaiderWrapper raider, boolean flag);

    Set<UUID> getHeroesOfTheVillage();
}
