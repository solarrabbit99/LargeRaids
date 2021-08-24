package com.solarrabbit.largeraids.v1_16;

import java.util.stream.Stream;
import com.solarrabbit.largeraids.AbstractRaiderConfig;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.server.v1_16_R3.EntityRaider;

public enum RaiderConfig implements AbstractRaiderConfig {
    PILLAGER(EntityType.PILLAGER, net.minecraft.server.v1_16_R3.EntityTypes.PILLAGER),
    VINDICATOR(EntityType.VINDICATOR, net.minecraft.server.v1_16_R3.EntityTypes.VINDICATOR),
    RAVAGER(EntityType.RAVAGER, net.minecraft.server.v1_16_R3.EntityTypes.RAVAGER),
    WITCH(EntityType.WITCH, net.minecraft.server.v1_16_R3.EntityTypes.WITCH),
    EVOKER(EntityType.EVOKER, net.minecraft.server.v1_16_R3.EntityTypes.EVOKER),
    ILLUSIONER(EntityType.ILLUSIONER, net.minecraft.server.v1_16_R3.EntityTypes.ILLUSIONER);

    private final EntityType type;
    private final net.minecraft.server.v1_16_R3.EntityTypes<? extends EntityRaider> nmsType;

    private RaiderConfig(EntityType type, net.minecraft.server.v1_16_R3.EntityTypes<? extends EntityRaider> nmsType) {
        this.type = type;
        this.nmsType = nmsType;
    }

    public static RaiderConfig valueOf(EntityType entity) {
        return Stream.of(values()).filter(value -> value.type == entity).findFirst().orElse(null);
    }

    public int getSpawnNumber(int wave) {
        return getMobsConfiguration().getIntegerList(this.name().toLowerCase()).get(wave - 1);
    }

    public net.minecraft.server.v1_16_R3.EntityTypes<? extends EntityRaider> getNMSType() {
        return this.nmsType;
    }

    private ConfigurationSection getMobsConfiguration() {
        return JavaPlugin.getPlugin(LargeRaids.class).getConfig().getConfigurationSection("raid.mobs");
    }
}
