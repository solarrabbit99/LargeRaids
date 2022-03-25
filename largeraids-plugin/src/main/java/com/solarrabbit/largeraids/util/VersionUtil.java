package com.solarrabbit.largeraids.util;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaiderWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftVexWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Vex;

public class VersionUtil {
    private static final String[] VERSIONS = new String[] { "v1_14_R1", "v1_15_R1", "v1_16_R3", "v1_17_R1",
            "v1_18_R1", "v1_18_R2" };

    public static AbstractBlockPositionWrapper getBlockPositionWrapper(Location location) {
        return getBlockPositionWrapper(location.getX(), location.getY(), location.getZ());
    }

    public static AbstractBlockPositionWrapper getBlockPositionWrapper(double x, double y, double z) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.BlockPositionWrapper(x, y, z);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.BlockPositionWrapper(x, y, z);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.BlockPositionWrapper(x, y, z);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.BlockPositionWrapper(x, y, z);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.BlockPositionWrapper(x, y, z);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.BlockPositionWrapper(x, y, z);
            default:
                return null;
        }
    }

    public static AbstractCraftRaidWrapper getCraftRaidWrapper(AbstractRaidWrapper wrapper) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftRaidWrapper(wrapper);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftRaidWrapper(wrapper);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftRaidWrapper(wrapper);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftRaidWrapper(wrapper);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftRaidWrapper(wrapper);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftRaidWrapper(wrapper);
            default:
                return null;
        }
    }

    public static AbstractCraftRaidWrapper getCraftRaidWrapper(Raid raid) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftRaidWrapper(raid);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftRaidWrapper(raid);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftRaidWrapper(raid);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftRaidWrapper(raid);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftRaidWrapper(raid);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftRaidWrapper(raid);
            default:
                return null;
        }
    }

    public static AbstractCraftRaiderWrapper getCraftRaiderWrapper(Raider raider) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftRaiderWrapper(raider);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftRaiderWrapper(raider);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftRaiderWrapper(raider);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftRaiderWrapper(raider);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftRaiderWrapper(raider);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftRaiderWrapper(raider);
            default:
                return null;
        }
    }

    public static AbstractCraftServerWrapper getCraftServerWrapper(Server server) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftServerWrapper(server);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftServerWrapper(server);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftServerWrapper(server);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftServerWrapper(server);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftServerWrapper(server);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftServerWrapper(server);
            default:
                return null;
        }
    }

    public static AbstractCraftWorldWrapper getCraftWorldWrapper(World world) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftWorldWrapper(world);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftWorldWrapper(world);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftWorldWrapper(world);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftWorldWrapper(world);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftWorldWrapper(world);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftWorldWrapper(world);
            default:
                return null;
        }
    }

    public static AbstractPlayerEntityWrapper getPlayerEntityWrapper(AbstractMinecraftServerWrapper server,
            AbstractWorldServerWrapper world,
            GameProfile profile) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.PlayerEntityWrapper(server, world, profile);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.PlayerEntityWrapper(server, world, profile);
            default:
                return null;
        }
    }

    public static AbstractPoiTypeWrapper getMasonPoiTypeWrapper() {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return com.solarrabbit.largeraids.v1_14.nms.PoiTypeWrapper.MASON;
            case "v1_15_R1":
                return com.solarrabbit.largeraids.v1_15.nms.PoiTypeWrapper.MASON;
            case "v1_16_R3":
                return com.solarrabbit.largeraids.v1_16.nms.PoiTypeWrapper.MASON;
            case "v1_17_R1":
                return com.solarrabbit.largeraids.v1_17.nms.PoiTypeWrapper.MASON;
            case "v1_18_R1":
                return com.solarrabbit.largeraids.v1_18_R1.nms.PoiTypeWrapper.MASON;
            case "v1_18_R2":
                return com.solarrabbit.largeraids.v1_18_R2.nms.PoiTypeWrapper.MASON;
            default:
                return null;
        }
    }

    public static AbstractCraftVexWrapper getCraftVexWrapper(Vex vex) {
        switch (getAPIVersion()) {
            case "v1_14_R1":
                return new com.solarrabbit.largeraids.v1_14.nms.CraftVexWrapper(vex);
            case "v1_15_R1":
                return new com.solarrabbit.largeraids.v1_15.nms.CraftVexWrapper(vex);
            case "v1_16_R3":
                return new com.solarrabbit.largeraids.v1_16.nms.CraftVexWrapper(vex);
            case "v1_17_R1":
                return new com.solarrabbit.largeraids.v1_17.nms.CraftVexWrapper(vex);
            case "v1_18_R1":
                return new com.solarrabbit.largeraids.v1_18_R1.nms.CraftVexWrapper(vex);
            case "v1_18_R2":
                return new com.solarrabbit.largeraids.v1_18_R2.nms.CraftVexWrapper(vex);
            default:
                return null;
        }
    }

    public static int getServerMinorVersion() {
        return getMinorVersion(getServerVersion());
    }

    public static boolean isSupported() {
        String apiVersion = getAPIVersion();
        for (String version : VERSIONS)
            if (version.equals(apiVersion))
                return true;
        return false;
    }

    public static int compare(String versionA, String versionB) {
        int majDiff = getMajorVersion(versionA) - getMajorVersion(versionB);
        if (majDiff != 0)
            return majDiff;
        int minorDiff = getMinorVersion(versionA) - getMinorVersion(versionB);
        if (minorDiff != 0)
            return minorDiff;
        return getPatchVersion(versionA) - getPatchVersion(versionB);
    }

    private static int getMajorVersion(String version) {
        String[] splits = version.split("\\.");
        return Integer.parseInt(splits[0]);
    }

    private static int getMinorVersion(String version) {
        String[] splits = version.split("\\.");
        return splits.length < 2 ? 0 : Integer.parseInt(splits[1]);
    }

    private static int getPatchVersion(String version) {
        String[] splits = version.split("\\.");
        return splits.length < 3 ? 0 : Integer.parseInt(splits[2]);
    }

    private static String getServerVersion() {
        return getCraftServerWrapper(Bukkit.getServer()).getServer().getServerVersion();
    }

    private static String getAPIVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
