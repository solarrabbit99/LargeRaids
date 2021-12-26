package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class RaidListener implements Listener {
    private static final Set<LargeRaid> currentRaids = new HashSet<>();
    private static final Set<Runnable> tickTasks = new HashSet<>();
    private static final Set<Consumer<LargeRaid>> tickIteratingTasks = new HashSet<>();
    private final LargeRaids plugin;

    public RaidListener(LargeRaids plugin) {
        this.plugin = plugin;
    }

    public static void addLargeRaid(LargeRaid raid) {
        currentRaids.add(raid);
    }

    public static void removeLargeRaid(LargeRaid raid) {
        currentRaids.remove(raid);
    }

    public static int getNumOfRegisteredRaids() {
        return currentRaids.size();
    }

    @EventHandler
    public void onSpawn(RaidSpawnWaveEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> largeRaid.spawnWave());
    }

    @EventHandler
    public void onTrigger(RaidTriggerEvent evt) {
        if (RaidListener.matchingLargeRaid(evt.getRaid()).isPresent())
            return;
        if (!plugin.getTriggerConfig().canNormalRaid())
            evt.setCancelled(true);
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> tick(), 0, 1);
    }

    private void tick() {
        for (LargeRaid largeRaid : currentRaids) {
            if (largeRaid.isActive() && largeRaid.getTotalRaidersAlive() == 0 && !largeRaid.isLoading()
                    && !largeRaid.isLastWave())
                largeRaid.triggerNextWave();
            for (Consumer<LargeRaid> task : tickIteratingTasks)
                task.accept(largeRaid);
        }

        for (Runnable task : tickTasks)
            task.run();
    }

    public static Optional<LargeRaid> matchingLargeRaid(Location location) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(location)).findFirst();
    }

    public static Optional<LargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

    public static void registerTickTask(Runnable task) {
        tickTasks.add(task);
    }

    public static void registerTickTask(Consumer<LargeRaid> task) {
        tickIteratingTasks.add(task);
    }

    public static void unregisterTickTask(Runnable task) {
        tickTasks.remove(task);
    }

    public static void unregisterTickTask(Consumer<LargeRaid> task) {
        tickIteratingTasks.remove(task);
    }

}