package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.solarrabbit.largeraids.AbstractLargeRaid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RaidListener implements Listener {
    private static final Set<AbstractLargeRaid> currentRaids = new HashSet<>();
    private final JavaPlugin plugin;

    public RaidListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void addLargeRaid(AbstractLargeRaid raid) {
        currentRaids.add(raid);
    }

    public static void removeLargeRaid(AbstractLargeRaid raid) {
        currentRaids.remove(raid);
    }

    @EventHandler
    public void onSpawn(RaidSpawnWaveEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> largeRaid.spawnNextWave());
    }

    @EventHandler
    public void onFinish(RaidFinishEvent evt) {
        Raid raid = evt.getRaid();
        matchingLargeRaid(raid).ifPresent(largeRaid -> {
            RaidStatus status = raid.getStatus();
            if (status == RaidStatus.VICTORY) {
                largeRaid.announceVictory();
            } else if (status == RaidStatus.LOSS) {
                largeRaid.announceDefeat();
            }
            currentRaids.remove(largeRaid);
        });
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            currentRaids.remove(largeRaid);
        });
    }

    public void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> tick(), 0, 1);
    }

    private void tick() {
        for (AbstractLargeRaid largeRaid : currentRaids) {
            if (!largeRaid.isLoading() && largeRaid.getTotalRaidersAlive() == 0 && !largeRaid.isLastWave()) {
                largeRaid.triggerNextWave();
            }
        }
    }

    public static Optional<AbstractLargeRaid> matchingLargeRaid(Location location) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(location)).findFirst();
    }

    private Optional<AbstractLargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }
}