package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;

import net.minecraft.server.v1_16_R3.MinecraftServer;

public class MinecraftServerWrapper implements AbstractMinecraftServerWrapper {
    final MinecraftServer server;

    MinecraftServerWrapper(MinecraftServer server) {
        this.server = server;
    }
}
