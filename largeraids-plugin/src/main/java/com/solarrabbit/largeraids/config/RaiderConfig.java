package com.solarrabbit.largeraids.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import com.solarrabbit.largeraids.raid.mob.EventMythicRaider;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.raid.mob.EventVanillaRaider;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;

public class RaiderConfig {
    private static final EntityType[] DEFAULT_RAIDER_TYPES = new EntityType[] { EntityType.PILLAGER,
            EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITCH, EntityType.EVOKER, EntityType.ILLUSIONER };
    private final ConfigurationSection config;
    private final Map<EventRaider, List<Integer>> mobsMap;

    RaiderConfig(ConfigurationSection config) {
        this.config = config;
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
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
            loadMythicRaiders(stringMappings);
    }

    private void loadVanillaRaiders(Map<String, List<Integer>> stringMappings) {
        for (EntityType type : DEFAULT_RAIDER_TYPES) {
            EventVanillaRaider vanillaRaider = new EventVanillaRaider(type);
            List<Integer> list = stringMappings.remove(type.name().toLowerCase());
            mobsMap.put(vanillaRaider, list);
        }
    }

    private void loadMythicRaiders(Map<String, List<Integer>> stringMappings) {
        MobManager manager = MythicMobs.inst().getMobManager();
        for (Entry<String, List<Integer>> entry : stringMappings.entrySet()) {
            MythicMob mob = manager.getMythicMob(entry.getKey());
            if (mob != null) {
                mobsMap.put(new EventMythicRaider(mob), entry.getValue());
            }
        }
    }

    private Map<String, List<Integer>> getStringMappings() {
        Set<String> keys = config.getKeys(false);
        Map<String, List<Integer>> mappings = new HashMap<>();
        for (String key : keys) {
            mappings.put(key, config.getIntegerList(key));
        }
        return mappings;
    }

}
