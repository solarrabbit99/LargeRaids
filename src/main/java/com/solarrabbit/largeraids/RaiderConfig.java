package com.solarrabbit.largeraids;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.bukkit.plugin.java.JavaPlugin;

public enum RaiderConfig {
    PILLAGER(EntityType.PILLAGER), VINDICATOR(EntityType.VINDICATOR), RAVAGER(EntityType.RAVAGER),
    WITCH(EntityType.WITCH), EVOKER(EntityType.EVOKER), ILLUSIONER(EntityType.ILLUSIONER);

    private final EntityType type;

    private RaiderConfig(EntityType type) {
        this.type = type;
    }

    public List<Raider> spawnWave(int wave, Location location) {
        int number = getSpawnNumber(wave);
        List<Raider> result = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            result.add((Raider) location.getWorld().spawnEntity(location, this.type));
        }
        return result;
    }

    public static RaiderConfig valueOf(EntityType entity) {
        return Stream.of(values()).filter(value -> value.type == entity).findFirst().orElse(null);
    }

    private int getSpawnNumber(int wave) {
        return getMobsConfiguration().getIntegerList(this.name().toLowerCase()).get(wave - 1);
    }

    private ConfigurationSection getMobsConfiguration() {
        return JavaPlugin.getPlugin(LargeRaids.class).getConfig().getConfigurationSection("raid.mobs");
    }
}
