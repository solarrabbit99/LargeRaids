package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class CraftWorldWrapper extends AbstractCraftWorldWrapper {

    public CraftWorldWrapper(World world) {
        super(world);
    }

    @Override
    public WorldServerWrapper getHandle() {
        return new WorldServerWrapper(((CraftWorld) this.world).getHandle());
    }

}
