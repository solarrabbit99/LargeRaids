package com.solarrabbit.largeraids.v1_14;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.village.AbstractVillages;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.GlobalPos;
import net.minecraft.server.v1_14_R1.MemoryModuleType;
import net.minecraft.server.v1_14_R1.VillagePlace;
import net.minecraft.server.v1_14_R1.VillagePlaceType;
import net.minecraft.server.v1_14_R1.WorldServer;
import net.minecraft.server.v1_14_R1.VillagePlace.Occupancy;

public class CustomVillages implements AbstractVillages {
    private static final VillagePlaceType JOB_TYPE = VillagePlaceType.m;
    private static final Predicate<VillagePlaceType> PRED_JOB = (type) -> type == JOB_TYPE;

    @Override
    public void addVillage(Location location, Runnable ifSuccess, Runnable ifFail) {
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = getManager(location);
        villageRecordManager.a(blockPos, JOB_TYPE);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        ((CraftVillager) villager).getHandle().getBehaviorController().setMemory(MemoryModuleType.JOB_SITE,
                GlobalPos.create(nmsWorld.getWorldProvider().getDimensionManager(), blockPos));
        villager.setAI(false);

        Villager villager2 = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                villager.remove();
                villager2.remove();

                if (villageRecordManager.a(PRED_JOB, blockPos, 1, Occupancy.IS_OCCUPIED) >= 1) {
                    ifSuccess.run();
                } else {
                    ifFail.run();
                }
            }
        };
        runnable.runTaskLater(getPlugin(), 100);
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
        return nmsWorld.B();
    }

}