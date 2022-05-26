package com.solarrabbit.largeraids.raid.mob.manager;

import com.solarrabbit.largeraids.util.BossBarCreator;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.Raider;

public interface BossRaiderManager extends CustomRaiderManager {
    default BossBar createBossBar(Raider boss) {
        return BossBarCreator.createRaidBossBar(boss);
    }
}
