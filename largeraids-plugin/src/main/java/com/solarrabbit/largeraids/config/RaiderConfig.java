package com.solarrabbit.largeraids.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.mob.Bomber;
import com.solarrabbit.largeraids.raid.mob.FireworkPillager;
import com.solarrabbit.largeraids.raid.mob.Juggernaut;
import com.solarrabbit.largeraids.raid.mob.MythicRaider;
import com.solarrabbit.largeraids.raid.mob.Necromancer;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRaiderRider;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.core.mobs.MobExecutor;

public class RaiderConfig {
    private static final EntityType[] DEFAULT_RAIDER_TYPES = new EntityType[] { EntityType.PILLAGER,
            EntityType.VINDICATOR, EntityType.RAVAGER, EntityType.WITCH, EntityType.EVOKER, EntityType.ILLUSIONER };
    private final Map<EventRaider, List<Integer>> mobsMap;
    private final Map<String, List<Integer>> stringMappings;

    RaiderConfig(ConfigurationSection config) {
        stringMappings = new HashMap<>();
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            stringMappings.put(key, config.getIntegerList(key));
        }

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
        loadVanillaRaiders();
        loadCustomRaiders();
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
            loadMythicRaiders();
    }

    private void loadVanillaRaiders() {
        for (EntityType type : DEFAULT_RAIDER_TYPES) {
            VanillaRaider vanillaRaider = new VanillaRaider(type);
            List<Integer> list = stringMappings.remove(type.name().toLowerCase());
            if (list != null)
                mobsMap.put(vanillaRaider, list);
            // Counterparts raiders riding ravagers
            VanillaRaiderRider raiderRider = new VanillaRaiderRider(type);
            List<Integer> riderList = stringMappings.remove(type.name().toLowerCase() + "rider");
            if (riderList != null)
                mobsMap.put(raiderRider, riderList);
        }
    }

    private void loadCustomRaiders() {
        FireworkPillager fireworkPillager = new FireworkPillager();
        List<Integer> list = stringMappings.remove("fireworkpillager");
        if (list != null)
            mobsMap.put(fireworkPillager, list);

        Bomber bomber = new Bomber();
        List<Integer> bomberList = stringMappings.remove("bomber");
        if (bomberList != null)
            mobsMap.put(bomber, bomberList);

        Necromancer necromancer = new Necromancer();
        List<Integer> necromancerList = stringMappings.remove("necromancer");
        if (necromancerList != null)
            mobsMap.put(necromancer, necromancerList);

        Juggernaut juggernaut = new Juggernaut();
        List<Integer> juggernautList = stringMappings.remove("juggernaut");
        if (juggernautList != null)
            mobsMap.put(juggernaut, juggernautList);
    }

    private void loadMythicRaiders() {
        MythicMobsLoader loader = new MythicMobsLoader();
        Bukkit.getPluginManager().registerEvents(loader, JavaPlugin.getPlugin(LargeRaids.class));
        // Load MythicMobs if plugin is loaded
        Optional<MythicBukkit> adapter = Optional.ofNullable(MythicBukkit.inst());
        adapter.map(MythicBukkit::getMobManager).ifPresent(loader::loadMobs);
    }

    private class MythicMobsLoader implements Listener {
        @EventHandler
        private void onMythicMobsLoad(MythicReloadedEvent evt) {
            loadMobs(evt.getInstance().getMobManager());
        }

        @EventHandler
        private void onPluginEnable(PluginEnableEvent evt) {
            Plugin plugin = evt.getPlugin();
            if (plugin.getName().equals("MythicMobs")) {
                loadMobs(MythicBukkit.inst().getMobManager());
            }
        }

        private void loadMobs(MobExecutor mobManager) {
            mobsMap.keySet().removeIf(mob -> mob instanceof MythicRaider);
            for (Entry<String, List<Integer>> entry : stringMappings.entrySet())
                mobManager.getMythicMob(entry.getKey())
                        .ifPresent(mob -> mobsMap.put(new MythicRaider(mob), entry.getValue()));
        }
    }

}
