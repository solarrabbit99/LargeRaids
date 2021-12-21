package com.solarrabbit.largeraids.nms;

import org.bukkit.entity.Raider;

public abstract class AbstractCraftRaiderWrapper {
    protected final Raider raider;

    public AbstractCraftRaiderWrapper(Raider raider) {
        this.raider = raider;
    }

    public abstract AbstractRaiderWrapper getHandle();
}
