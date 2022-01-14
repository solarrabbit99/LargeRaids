package com.solarrabbit.largeraids.nms;

import org.bukkit.Raid;

public abstract class AbstractCraftRaidWrapper {
    protected final Raid raid;

    public AbstractCraftRaidWrapper(Raid raid) {
        this.raid = raid;
    }

    public Raid getRaid() {
        return this.raid;
    }

    public abstract AbstractRaidWrapper getHandle();

}
