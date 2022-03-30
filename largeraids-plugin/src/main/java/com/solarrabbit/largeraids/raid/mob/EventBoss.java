package com.solarrabbit.largeraids.raid.mob;

import com.solarrabbit.largeraids.util.BossBarCreator;

import org.bukkit.entity.Raider;

public interface EventBoss extends EventRaider {
    default void createBossBar(Raider boss) {
        BossBarCreator.createRaidBossBar(boss);
    }
}
