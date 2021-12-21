package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftRaiderWrapper;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftRaider;
import org.bukkit.entity.Raider;

public class CraftRaiderWrapper extends AbstractCraftRaiderWrapper {

    public CraftRaiderWrapper(Raider raider) {
        super(raider);
    }

    public RaiderWrapper getHandle() {
        return new RaiderWrapper(((CraftRaider) this.raider).getHandle());
    }

}
