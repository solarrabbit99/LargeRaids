package com.solarrabbit.largeraids.v1_17;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.village.AbstractVillages;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.Level;

public class CustomVillages implements AbstractVillages {
    private static final PoiType JOB_TYPE = PoiType.MASON;
    private static final Predicate<PoiType> PRED_JOB = (type) -> type == JOB_TYPE;

    @Override
    public void addVillage(Location location, Runnable ifSuccess, Runnable ifFail) {
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = getManager(location);
        villageRecordManager.add(blockPos, JOB_TYPE);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        ((LivingEntity) ((CraftVillager) villager).getHandle()).getBrain().setMemory(MemoryModuleType.JOB_SITE,
                GlobalPos.of(((Level) nmsWorld).dimension(), blockPos));
        villager.setAI(false);

        Villager villager2 = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                villager.remove();
                villager2.remove();

                if (villageRecordManager.getCountInRange(PRED_JOB, blockPos, 1, Occupancy.IS_OCCUPIED) >= 1) {
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
