package com.solarrabbit.largeraids.v1_17;

import java.util.stream.Stream;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.world.entity.raid.Raider;

enum RaiderConfig {
    PILLAGER(EntityType.PILLAGER, net.minecraft.world.entity.EntityType.PILLAGER),
    VINDICATOR(EntityType.VINDICATOR, net.minecraft.world.entity.EntityType.VINDICATOR),
    RAVAGER(EntityType.RAVAGER, net.minecraft.world.entity.EntityType.RAVAGER),
    WITCH(EntityType.WITCH, net.minecraft.world.entity.EntityType.WITCH),
    EVOKER(EntityType.EVOKER, net.minecraft.world.entity.EntityType.EVOKER),
    ILLUSIONER(EntityType.ILLUSIONER, net.minecraft.world.entity.EntityType.ILLUSIONER);

    private final EntityType type;
    private final net.minecraft.world.entity.EntityType<? extends Raider> nmsType;

    private RaiderConfig(EntityType type, net.minecraft.world.entity.EntityType<? extends Raider> nmsType) {
        this.type = type;
        this.nmsType = nmsType;
    }

    public static RaiderConfig valueOf(EntityType entity) {
        return Stream.of(values()).filter(value -> value.type == entity).findFirst().orElse(null);
    }

    public int getSpawnNumber(int wave) {
        return getMobsConfiguration().getIntegerList(this.name().toLowerCase()).get(wave - 1);
    }

    public net.minecraft.world.entity.EntityType<? extends Raider> getNMSType() {
        return this.nmsType;
    }

    private ConfigurationSection getMobsConfiguration() {
        return JavaPlugin.getPlugin(LargeRaids.class).getConfig().getConfigurationSection("raid.mobs");
    }
}
