package com.solarrabbit.largeraids.v1_17;

import com.solarrabbit.largeraids.AbstractVillages;
import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.Level;

public class CustomVillages implements AbstractVillages {
    private static final PoiType JOB_TYPE = PoiType.MASON;

    @Override
    public void addVillage(Location location) {
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = nmsWorld.getPoiManager();
        villageRecordManager.add(blockPos, JOB_TYPE);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        ((LivingEntity) ((CraftVillager) villager).getHandle()).getBrain().setMemory(MemoryModuleType.JOB_SITE,
                GlobalPos.of(((Level) nmsWorld).dimension(), blockPos));
        villager.setAI(false);

        Villager villager2 = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            villager.remove();
            villager2.remove();
        }, 17);
    }

    @Override
    public boolean removeVillage(Location location) {
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = getBlockPosFromLocation(location);
        PoiManager villageRecordManager = nmsWorld.getPoiManager();
        if (!villageRecordManager.existsAtPosition(JOB_TYPE, blockPos)) {
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
