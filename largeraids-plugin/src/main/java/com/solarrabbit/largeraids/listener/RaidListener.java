package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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
    private static final Set<Runnable> tickTasks = new HashSet<>();
    private static final Set<Consumer<AbstractLargeRaid>> tickIteratingTasks = new HashSet<>();
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

    public static int getNumOfRegisteredRaids() {
        return currentRaids.size();
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
            for (Consumer<AbstractLargeRaid> task : tickIteratingTasks) {
                task.accept(largeRaid);
            }
        }

        for (Runnable task : tickTasks) {
            task.run();
        }
    }

    public static Optional<AbstractLargeRaid> matchingLargeRaid(Location location) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(location)).findFirst();
    }

    public static Optional<AbstractLargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

    public static void registerTickTask(Runnable task) {
        tickTasks.add(task);
    }

    public static void registerTickTask(Consumer<AbstractLargeRaid> task) {
        tickIteratingTasks.add(task);
    }

    public static void unregisterTickTask(Runnable task) {
        tickTasks.remove(task);
    }

    public static void unregisterTickTask(Consumer<AbstractLargeRaid> task) {
        tickIteratingTasks.remove(task);
    }

}