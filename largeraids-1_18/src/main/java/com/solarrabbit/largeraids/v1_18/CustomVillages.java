package com.solarrabbit.largeraids.v1_18;

import com.solarrabbit.largeraids.village.AbstractVillages;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class CustomVillages implements AbstractVillages {
    private static final PoiType JOB_TYPE = PoiType.MASON;

    @Override
    public boolean addVillage(Location location) {
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = getManager(location);
        villageRecordManager.add(blockPos, JOB_TYPE);
        return villageRecordManager.take(JOB_TYPE.getPredicate(), pos -> pos.equals(blockPos), blockPos, 1).isPresent();
    }

    @Override
    public void removeVillage(Location location) {
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = getManager(location);
        villageRecordManager.remove(blockPos);
    }

    private BlockPos getBlockPosFromLocation(Location loc) {
        return new BlockPos(loc.getX(), loc.getY(), loc.getZ());
    }

    private PoiManager getManager(Location loc) {
        ServerLevel nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        return nmsWorld.getPoiManager();
    }

}
