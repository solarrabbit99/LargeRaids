package com.solarrabbit.largeraids.raid.mob;

import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Ravager;
import org.bukkit.event.Listener;

public class KingRaider implements Boss, RiderRaider, Listener {
    private final Raider rider;
    private final Ravager ravager;
    private final BossBar bossBar;

    public KingRaider(Raider rider, Ravager ravager, BossBar bossBar) {
        this.rider = rider;
        this.ravager = ravager;
        this.bossBar = bossBar;
    }

    @Override
    public LivingEntity getBukkitEntity() {
        return rider;
    }

    @Override
    public Ravager getVehicle() {
        return ravager;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

}
