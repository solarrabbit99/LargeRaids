package com.solarrabbit.largeraids.v1_17.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.server.level.ServerLevel;

public class WorldServerWrapper implements AbstractWorldServerWrapper {
    final ServerLevel server;

    WorldServerWrapper(ServerLevel server) {
        this.server = server;
    }

    @Override
    public RaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos) {
        return new RaidWrapper(this.server.getRaidAt(((BlockPositionWrapper) blockPos).blockPos));
    }

    @Override
    public RaidsWrapper getRaids() {
        return new RaidsWrapper(this.server.getRaids());
    }

}
