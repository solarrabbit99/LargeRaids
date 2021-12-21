package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.server.v1_16_R3.EntityRaider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final EntityRaider raider;

    RaiderWrapper(EntityRaider raider) {
        this.raider = raider;
    }
}
