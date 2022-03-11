package com.solarrabbit.largeraids.v1_18_R2.nms;

import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;

import net.minecraft.server.MinecraftServer;

public class MinecraftServerWrapper implements AbstractMinecraftServerWrapper {
    final MinecraftServer server;

    MinecraftServerWrapper(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public String getServerVersion() {
        return server.G();
    }
}
