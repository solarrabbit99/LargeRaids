package com.solarrabbit.largeraids.v1_18_R1.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.world.entity.raid.Raider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final Raider raider;

    RaiderWrapper(Raider raider) {
        this.raider = raider;
    }

    @Override
    public RaidWrapper getCurrentRaid() {
        return new RaidWrapper(raider.getCurrentRaid());
    }
}
