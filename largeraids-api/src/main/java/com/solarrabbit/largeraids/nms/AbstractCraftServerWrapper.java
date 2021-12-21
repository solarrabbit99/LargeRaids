package com.solarrabbit.largeraids.nms;

import org.bukkit.Server;

public abstract class AbstractCraftServerWrapper {
    protected final Server server;

    public AbstractCraftServerWrapper(Server server) {
        this.server = server;
    }

    public abstract AbstractMinecraftServerWrapper getServer();
}
