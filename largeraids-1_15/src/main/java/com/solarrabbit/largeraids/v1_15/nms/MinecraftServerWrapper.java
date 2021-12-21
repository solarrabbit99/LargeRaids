package com.solarrabbit.largeraids.v1_15.nms;

import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;

import net.minecraft.server.v1_15_R1.MinecraftServer;

public class MinecraftServerWrapper implements AbstractMinecraftServerWrapper {
    final MinecraftServer server;

    MinecraftServerWrapper(MinecraftServer server) {
        this.server = server;
    }
}
