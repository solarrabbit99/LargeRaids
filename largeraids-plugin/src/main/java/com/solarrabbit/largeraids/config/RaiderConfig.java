package com.solarrabbit.largeraids.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.mob.Bomber;
import com.solarrabbit.largeraids.raid.mob.FireworkPillager;
import com.solarrabbit.largeraids.raid.mob.KingRaider;
import com.solarrabbit.largeraids.raid.mob.MythicRaider;
import com.solarrabbit.largeraids.raid.mob.Necromancer;
import com.solarrabbit.largeraids.raid.mob.Raider;
import com.solarrabbit.largeraids.raid.mob.VanillaRaider;
import com.solarrabbit.largeraids.raid.mob.VanillaRiderRaider;
import com.solarrabbit.largeraids.raid.mob.manager.BomberManager;
import com.solarrabbit.largeraids.raid.mob.manager.FireworkPillagerManager;
import com.solarrabbit.largeraids.raid.mob.manager.KingRaiderManager;
import com.solarrabbit.largeraids.raid.mob.manager.MobManagers;
import com.solarrabbit.largeraids.raid.mob.manager.MythicRaiderManager;
import com.solarrabbit.largeraids.raid.mob.manager.NecromancerManager;
import com.solarrabbit.largeraids.raid.mob.manager.VanillaRaiderManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private final Map<Function<Location, ? extends Raider>, List<Integer>> mobsSupplierMap;
    private final Map<Function<Location, MythicRaider>, List<Integer>> mythicMobsSupplierMap;
    private final Map<String, List<Integer>> stringMappings;
    private final MobManagers mobManagers;

    RaiderConfig(ConfigurationSection config) {
        stringMappings = new HashMap<>();
        Set<String> keys = config.getKeys(false);
        for (String key : keys)
            stringMappings.put(key, config.getIntegerList(key));
        // TODO Not optimal way of getting vanilla raider manager
        mobManagers = JavaPlugin.getPlugin(LargeRaids.class).getMobManagers();
        mobsSupplierMap = new HashMap<>();
        mythicMobsSupplierMap = new HashMap<>();
        init();
    }

    public Map<Function<Location, ? extends Raider>, Integer> getWaveMobs(int wave) {
        Map<Function<Location, ? extends Raider>, Integer> mobsSuppliers = new HashMap<>();
        mobsSupplierMap.forEach((supplier, list) -> mobsSuppliers.put(supplier, list.get(wave - 1)));
        mythicMobsSupplierMap.forEach((supplier, list) -> mobsSuppliers.put(supplier, list.get(wave - 1)));
        return mobsSuppliers;
    }

    private void init() {
        loadVanillaRaiders();
        loadCustomRaiders();
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
            loadMythicRaiders();
    }

    private void loadVanillaRaiders() {
        VanillaRaiderManager manager = (VanillaRaiderManager) mobManagers.getMobManager(VanillaRaider.class);
        for (EntityType type : DEFAULT_RAIDER_TYPES) {
            Function<Location, VanillaRaider> raiderSupplier = (loc) -> manager.spawn(loc, type);
            List<Integer> list = stringMappings.remove(type.name().toLowerCase());
            if (list != null)
                mobsSupplierMap.put(raiderSupplier, list);
            // Counterparts raiders riding ravagers
            Function<Location, VanillaRiderRaider> riderRaiderSupplier = (loc) -> manager.spawnRider(loc, type);
            List<Integer> riderList = stringMappings.remove(type.name().toLowerCase() + "rider");
            if (riderList != null)
                mobsSupplierMap.put(riderRaiderSupplier, riderList);
        }
    }

    private void loadCustomRaiders() {
        FireworkPillagerManager fireworkPillagerManager = (FireworkPillagerManager) mobManagers
                .getMobManager(FireworkPillager.class);
        Function<Location, FireworkPillager> fireworkPillagerSupplier = (loc) -> fireworkPillagerManager.spawn(loc);
        List<Integer> list = stringMappings.remove("fireworkpillager");
        if (list != null)
            mobsSupplierMap.put(fireworkPillagerSupplier, list);

        BomberManager bomberManager = (BomberManager) mobManagers.getMobManager(Bomber.class);
        Function<Location, Bomber> bomberSupplier = (loc) -> bomberManager.spawn(loc);
        List<Integer> bomberList = stringMappings.remove("bomber");
        if (list != null)
            mobsSupplierMap.put(bomberSupplier, bomberList);

        NecromancerManager necromancerManager = (NecromancerManager) mobManagers.getMobManager(Necromancer.class);
        Function<Location, Necromancer> necromancerSupplier = (loc) -> necromancerManager.spawn(loc);
        List<Integer> necromancerList = stringMappings.remove("necromancer");
        if (necromancerList != null)
            mobsSupplierMap.put(necromancerSupplier, necromancerList);

        KingRaiderManager kingRaiderManager = (KingRaiderManager) mobManagers.getMobManager(KingRaider.class);
        Function<Location, KingRaider> kingRaiderSupplier = (loc) -> kingRaiderManager.spawn(loc);
        List<Integer> kingRaiderList = stringMappings.remove("kingraider");
        if (kingRaiderList != null)
            mobsSupplierMap.put(kingRaiderSupplier, kingRaiderList);
    }

    private void loadMythicRaiders() {
        MythicMobsLoader loader = new MythicMobsLoader();
        JavaPlugin plugin = JavaPlugin.getPlugin(LargeRaids.class);
        MythicReloadedEvent.getHandlerList().unregister(plugin);
        PluginEnableEvent.getHandlerList().unregister(plugin);
        Bukkit.getPluginManager().registerEvents(loader, plugin);
        // Load MythicMobs if plugin is (re)loaded
        Optional<MythicBukkit> adapter = Optional.ofNullable(MythicBukkit.inst());
        adapter.map(MythicBukkit::getMobManager).ifPresent(loader::loadMobs);
    }

    private class MythicMobsLoader implements Listener {
        /**
         * Handles case when MythicMobs is reloaded.
         */
        @EventHandler
        private void onMythicMobsLoad(MythicReloadedEvent evt) {
            loadMobs(evt.getInstance().getMobManager());
        }

        /**
         * Handles case when MythicMobs is loaded after LargeRaids.
         */
        @EventHandler
        private void onPluginEnable(PluginEnableEvent evt) {
            Plugin plugin = evt.getPlugin();
            if (plugin.getName().equals("MythicMobs"))
                loadMobs(MythicBukkit.inst().getMobManager());
        }

        private void loadMobs(MobExecutor mobManager) {
            mythicMobsSupplierMap.clear();
            MythicRaiderManager mythicRaiderManager = (MythicRaiderManager) mobManagers
                    .getMobManager(MythicRaider.class);
            for (Entry<String, List<Integer>> entry : stringMappings.entrySet())
                mobManager.getMythicMob(entry.getKey())
                        .ifPresent(mob -> {
                            Function<Location, MythicRaider> mythicRaiderSupplier = (loc) -> mythicRaiderManager
                                    .spawn(loc, mob);
                            mythicMobsSupplierMap.put(mythicRaiderSupplier, entry.getValue());
                        });
        }
    }

}
