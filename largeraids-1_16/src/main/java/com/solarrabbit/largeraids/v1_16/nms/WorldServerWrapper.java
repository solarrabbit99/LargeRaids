package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.server.v1_16_R3.WorldServer;

public class WorldServerWrapper implements AbstractWorldServerWrapper {
    final WorldServer server;

    WorldServerWrapper(WorldServer server) {
        this.server = server;
    }

    @Override
    public RaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos) {
        return new RaidWrapper(this.server.b_(((BlockPositionWrapper) blockPos).blockPos));
    }

    @Override
    public RaidsWrapper getRaids() {
        return new RaidsWrapper(this.server.getPersistentRaid());
    }

    @Override
    public VillageManagerWrapper getVillageRecordManager() {
        return new VillageManagerWrapper(this.server.y());
    }
}
