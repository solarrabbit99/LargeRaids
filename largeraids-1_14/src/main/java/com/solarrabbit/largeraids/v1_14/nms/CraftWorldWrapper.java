package com.solarrabbit.largeraids.v1_14.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

public class CraftWorldWrapper extends AbstractCraftWorldWrapper {

    public CraftWorldWrapper(World world) {
        super(world);
    }

    @Override
    public WorldServerWrapper getHandle() {
        return new WorldServerWrapper(((CraftWorld) this.world).getHandle());
    }

}
