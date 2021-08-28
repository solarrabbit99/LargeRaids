package com.solarrabbit.largeraids.v1_17;

import com.solarrabbit.largeraids.AbstractVillages;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class CustomVillages implements AbstractVillages {

    @Override
    public void addVillage(Location location) {
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = nmsWorld.getPoiManager();
        villageRecordManager.add(blockPos, PoiType.UNEMPLOYED);
    }

    @Override
    public boolean removeVillage(Location location) {
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = nmsWorld.getPoiManager();
        if (!villageRecordManager.existsAtPosition(PoiType.UNEMPLOYED, blockPos)) {
            return false;
        } else {
            villageRecordManager.remove(blockPos);
            return true;
        }
    }

    private BlockPos getBlockPosFromLocation(Location loc) {
        return new BlockPos(loc.getX(), loc.getY(), loc.getZ());
    }

}
