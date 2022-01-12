package com.solarrabbit.largeraids.village;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractVillageManagerWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Location;

public class VillageManager {
    private static final AbstractPoiTypeWrapper JOB_TYPE = VersionUtil.getMasonPoiTypeWrapper();

    public boolean addVillage(@Nonnull Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractVillageManagerWrapper villageRecordManager = getManager(location);
        villageRecordManager.add(blockPos, JOB_TYPE);
        return villageRecordManager.take(JOB_TYPE.getPredicate(), pos -> pos.equals(blockPos), blockPos, 1).isPresent();
    }

    public void removeVillage(@Nonnull Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractVillageManagerWrapper villageRecordManager = getManager(location);
        villageRecordManager.remove(blockPos);
    }

    private AbstractVillageManagerWrapper getManager(@Nonnull Location loc) {
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(loc.getWorld()).getHandle();
        return level.getVillageRecordManager();
    }
}
