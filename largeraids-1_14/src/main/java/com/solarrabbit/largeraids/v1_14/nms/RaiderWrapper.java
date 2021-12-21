package com.solarrabbit.largeraids.v1_14.nms;

import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;

import net.minecraft.server.v1_14_R1.EntityRaider;

public class RaiderWrapper implements AbstractRaiderWrapper {
    final EntityRaider raider;

    RaiderWrapper(EntityRaider raider) {
        this.raider = raider;
    }
}
