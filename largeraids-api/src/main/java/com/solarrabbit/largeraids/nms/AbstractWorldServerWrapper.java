package com.solarrabbit.largeraids.nms;

public interface AbstractWorldServerWrapper {
    AbstractRaidWrapper getRaidAt(AbstractBlockPositionWrapper blockPos);

    AbstractRaidsWrapper getRaids();

    AbstractVillageManagerWrapper getVillageRecordManager();
}
