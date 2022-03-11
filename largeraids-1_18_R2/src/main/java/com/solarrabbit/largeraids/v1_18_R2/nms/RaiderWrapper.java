package com.solarrabbit.largeraids.v1_18_R2.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.world.entity.raid.EntityRaider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final EntityRaider raider;

    RaiderWrapper(EntityRaider raider) {
        this.raider = raider;
    }

    @Override
    public RaidWrapper getCurrentRaid() {
        return new RaidWrapper(raider.fN());
    }
}
