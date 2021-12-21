package com.solarrabbit.largeraids.v1_15.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftServerWrapper;

import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;

public class CraftServerWrapper extends AbstractCraftServerWrapper {

    public CraftServerWrapper(Server server) {
        super(server);
    }

    @Override
    public MinecraftServerWrapper getServer() {
        return new MinecraftServerWrapper(((CraftServer) this.server).getServer());
    }

}
