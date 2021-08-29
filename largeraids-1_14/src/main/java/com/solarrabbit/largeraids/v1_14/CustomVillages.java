package com.solarrabbit.largeraids.v1_14;

import com.solarrabbit.largeraids.AbstractVillages;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.GlobalPos;
import net.minecraft.server.v1_14_R1.MemoryModuleType;
import net.minecraft.server.v1_14_R1.VillagePlace;
import net.minecraft.server.v1_14_R1.VillagePlaceType;
import net.minecraft.server.v1_14_R1.WorldServer;

public class CustomVillages implements AbstractVillages {
    private static final VillagePlaceType JOB_TYPE = VillagePlaceType.m;

    @Override
    public void addVillage(Location location) {
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = nmsWorld.B();
        villageRecordManager.a(blockPos, JOB_TYPE);

        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        ((CraftVillager) villager).getHandle().getBehaviorController().setMemory(MemoryModuleType.JOB_SITE,
                GlobalPos.create(nmsWorld.getWorldProvider().getDimensionManager(), blockPos));
        villager.setAI(false);

        Villager villager2 = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            villager.remove();
            villager2.remove();
        }, 100);
    }

    @Override
    public boolean removeVillage(Location location) {
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPos = getBlockPosFromLocation(location);
        VillagePlace villageRecordManager = nmsWorld.B();
        if (!villageRecordManager.a(blockPos, (type) -> type == JOB_TYPE)) {
            return false;
        } else {
            villageRecordManager.a(blockPos);
            return true;
        }
    }

    private BlockPosition getBlockPosFromLocation(Location loc) {
        return new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
    }

}