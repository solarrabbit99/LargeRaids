package com.solarrabbit.largeraids.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.raid.mob.EventVanillaRaider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class RaiderConfig {
    private static final EntityType[] DEFAULT_RAIDER_TYPES = new EntityType[] { EntityType.PILLAGER,
            EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITCH, EntityType.EVOKER, EntityType.ILLUSIONER };
    private final JavaPlugin plugin;
    private final Map<EventRaider, List<Integer>> mobsMap;

    public RaiderConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        mobsMap = new HashMap<>();
        init();
    }

    public List<EventRaider> getWaveMobs(int wave) {
        List<EventRaider> mobs = new ArrayList<>();
        mobsMap.forEach((raider, list) -> {
            int number = list.get(wave - 1);
            for (int i = 0; i < number; i++) {
                mobs.add(raider);
            }
        });
        return mobs;
    }

    private void init() {
        Map<String, List<Integer>> stringMappings = getStringMappings();
        loadVanillaRaiders(stringMappings);
    }

    private void loadVanillaRaiders(Map<String, List<Integer>> stringMappings) {
        for (EntityType type : DEFAULT_RAIDER_TYPES) {
            EventVanillaRaider vanillaRaider = new EventVanillaRaider(type);
            List<Integer> list = stringMappings.get(type.name().toLowerCase());
            mobsMap.put(vanillaRaider, list);
        }
    }

    private Map<String, List<Integer>> getStringMappings() {
        ConfigurationSection mobsConfigSection = plugin.getConfig().getConfigurationSection("raid.mobs");
        Set<String> keys = mobsConfigSection.getKeys(false);
        Map<String, List<Integer>> mappings = new HashMap<>();
        for (String key : keys) {
            mappings.put(key, mobsConfigSection.getIntegerList(key));
        }
        return mappings;
    }

}
