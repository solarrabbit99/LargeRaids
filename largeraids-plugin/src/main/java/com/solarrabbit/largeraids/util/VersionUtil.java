package com.solarrabbit.largeraids.util;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaiderWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.village.AbstractVillages;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Raider;

public class VersionUtil {
    private static final String[] VERSIONS = new String[] { "v1_14_R1", "v1_15_R1", "v1_16_R3", "v1_17_R1",
            "v1_18_R1" };

    public static AbstractVillages getVillageManager() {
        if (getVersion().equals("v1_18_R1"))
            return new com.solarrabbit.largeraids.v1_18.CustomVillages();
        if (getVersion().equals("v1_17_R1"))
            return new com.solarrabbit.largeraids.v1_17.CustomVillages();
        if (getVersion().equals("v1_16_R3"))
            return new com.solarrabbit.largeraids.v1_16.CustomVillages();
        if (getVersion().equals("v1_15_R1"))
            return new com.solarrabbit.largeraids.v1_15.CustomVillages();
        if (getVersion().equals("v1_14_R1"))
            return new com.solarrabbit.largeraids.v1_14.CustomVillages();
        return null;
    }

    public static AbstractBlockPositionWrapper getBlockPositionWrapper(Location location) {
        return getBlockPositionWrapper(location.getX(), location.getY(), location.getZ());
    }

    public static AbstractBlockPositionWrapper getBlockPositionWrapper(double x, double y, double z) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.BlockPositionWrapper(x, y, z);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.BlockPositionWrapper(x, y, z);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.BlockPositionWrapper(x, y, z);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.BlockPositionWrapper(x, y, z);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.BlockPositionWrapper(x, y, z);
            default:
                return null;
        }
    }

    public static AbstractCraftRaidWrapper getCraftRaidWrapper(AbstractRaidWrapper wrapper) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftRaidWrapper(wrapper);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftRaidWrapper(wrapper);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftRaidWrapper(wrapper);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftRaidWrapper(wrapper);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.CraftRaidWrapper(wrapper);
            default:
                return null;
        }
    }

    public static AbstractCraftRaiderWrapper getCraftRaiderWrapper(Raider raider) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftRaiderWrapper(raider);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftRaiderWrapper(raider);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftRaiderWrapper(raider);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftRaiderWrapper(raider);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.CraftRaiderWrapper(raider);
            default:
                return null;
        }
    }

    public static AbstractCraftServerWrapper getCraftServerWrapper(Server server) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftServerWrapper(server);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftServerWrapper(server);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftServerWrapper(server);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftServerWrapper(server);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.CraftServerWrapper(server);
            default:
                return null;
        }
    }

    public static AbstractCraftWorldWrapper getCraftWorldWrapper(World world) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftWorldWrapper(world);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftWorldWrapper(world);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftWorldWrapper(world);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftWorldWrapper(world);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.CraftWorldWrapper(world);
            default:
                return null;
        }
    }

    public static AbstractPlayerEntityWrapper getPlayerEntityWrapper(AbstractMinecraftServerWrapper server,
            AbstractWorldServerWrapper world,
            GameProfile profile) {
        switch (getVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18.nms.PlayerEntityWrapper(server, world, profile);
            default:
                return null;
        }
    }

    public static boolean isAtLeast(String version) {
        boolean hasMet = false;
        String currentVersion = getVersion();
        for (int i = 0; i < VERSIONS.length; i++) {
            if (!hasMet && VERSIONS[i].equals(version))
                hasMet = true;
            if (VERSIONS[i].equals(currentVersion))
                return hasMet;
        }
        return false;
    }

    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
