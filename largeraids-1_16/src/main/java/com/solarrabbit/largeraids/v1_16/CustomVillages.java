package com.solarrabbit.largeraids.v1_16;

import com.solarrabbit.largeraids.village.AbstractVillages;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.VillagePlace;
import net.minecraft.server.v1_16_R3.VillagePlaceType;
import net.minecraft.server.v1_16_R3.WorldServer;

public class CustomVillages implements AbstractVillages {
    private static final VillagePlaceType JOB_TYPE = VillagePlaceType.g;

    @Override
    public boolean addVillage(Location location) {
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = getManager(location);
        villageRecordManager.a(blockPos, JOB_TYPE);
        return villageRecordManager.a(JOB_TYPE.c(), pos -> pos.equals(blockPos), blockPos, 1).isPresent();
    }

    @Override
    public void removeVillage(Location location) {
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = getManager(location);
        villageRecordManager.a(blockPos);
    }

    private BlockPosition getBlockPosFromLocation(Location loc) {
        return new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    private VillagePlace getManager(Location loc) {
        WorldServer nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        return nmsWorld.y();
    }

}