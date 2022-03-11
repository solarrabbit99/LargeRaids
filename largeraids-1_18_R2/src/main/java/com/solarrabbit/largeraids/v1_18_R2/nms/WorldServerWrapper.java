package com.solarrabbit.largeraids.v1_18_R2.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.server.level.WorldServer;

public class WorldServerWrapper implements AbstractWorldServerWrapper {
    final WorldServer server;

    WorldServerWrapper(WorldServer server) {
        this.server = server;
    }

    @Override
    public RaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos) {
        return new RaidWrapper(this.server.c(((BlockPositionWrapper) blockPos).blockPos));
    }

    @Override
    public RaidsWrapper getRaids() {
        return new RaidsWrapper(this.server.A());
    }

    @Override
    public VillageManagerWrapper getVillageRecordManager() {
        return new VillageManagerWrapper(this.server.z());
    }

}
