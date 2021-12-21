package com.solarrabbit.largeraids.nms;

import org.bukkit.World;

public abstract class AbstractCraftWorldWrapper {
    protected final World world;

    public AbstractCraftWorldWrapper(World world) {
        this.world = world;
    }

    public abstract AbstractWorldServerWrapper getHandle();
}
