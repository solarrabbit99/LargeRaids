package com.solarrabbit.largeraids.v1_16;

import java.util.function.Predicate;
import com.solarrabbit.largeraids.AbstractVillages;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.GlobalPos;
import net.minecraft.server.v1_16_R3.MemoryModuleType;
import net.minecraft.server.v1_16_R3.VillagePlace;
import net.minecraft.server.v1_16_R3.VillagePlaceType;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.minecraft.server.v1_16_R3.VillagePlace.Occupancy;

public class CustomVillages implements AbstractVillages {
    private static final VillagePlaceType JOB_TYPE = VillagePlaceType.g;
    private static final Predicate<VillagePlaceType> PRED_JOB = (type) -> type == JOB_TYPE;

    @Override
    public void addVillage(Location location, Runnable ifSuccess, Runnable ifFail) {
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = getManager(location);
        villageRecordManager.a(blockPos, JOB_TYPE);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        ((CraftVillager) villager).getHandle().getBehaviorController().setMemory(MemoryModuleType.JOB_SITE,
                GlobalPos.create(nmsWorld.getDimensionKey(), blockPos));
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
        runnable.runTaskLater(getPlugin(), 17);
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