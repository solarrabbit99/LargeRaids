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

public class BukkitRaidListener implements Listener {
    private final Set<LargeRaid> currentRaids = new HashSet<>();
    private final Set<Runnable> tickTasks = new HashSet<>();
    private final Set<Consumer<LargeRaid>> tickIteratingTasks = new HashSet<>();
    private final LargeRaids plugin;
    private boolean isIdle;

    public BukkitRaidListener(LargeRaids plugin) {
        this.plugin = plugin;
        isIdle = false;
    }

    public void addLargeRaid(LargeRaid raid) {
        currentRaids.add(raid);
    }

    public void removeLargeRaid(LargeRaid raid) {
        currentRaids.remove(raid);
    }

    public boolean isIdle() {
        return isIdle;
    }

    /**
     * Idles the listener, mainly used to signify that any {@link RaidTriggerEvent}
     * fired after part of a {@link LargeRaid}.
     */
    public void setIdle() {
        isIdle = true;
    }

    /**
     * Re-activates the listener, mainly used to signify that any
     * {@link RaidTriggerEvent} fired after are vanilla.
     */
    public void setActive() {
        isIdle = false;
    }

    public int getNumOfRegisteredRaids() {
        return currentRaids.size();
    }

    @EventHandler
    public void onSpawn(RaidSpawnWaveEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            setIdle();
            largeRaid.spawnWave();
            setActive();
        });
    }

    @EventHandler
    public void onTrigger(RaidTriggerEvent evt) {
        if (isIdle())
            return;
        if (!plugin.getTriggerConfig().canNormalRaid())
            evt.setCancelled(true);
    }

    @EventHandler
    public void onFinish(RaidFinishEvent evt) {
        Raid raid = evt.getRaid();
        matchingLargeRaid(raid).ifPresent(largeRaid -> {
            RaidStatus status = raid.getStatus();
            if (status == RaidStatus.VICTORY)
                largeRaid.announceVictory();
            else if (status == RaidStatus.LOSS)
                largeRaid.announceDefeat();
            currentRaids.remove(largeRaid);
        });
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> currentRaids.remove(largeRaid));
    }

    public void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> tick(), 0, 1);
    }

    private void tick() {
        for (LargeRaid largeRaid : currentRaids) {
            if (largeRaid.isActive() && largeRaid.getTotalRaidersAlive() == 0 && !largeRaid.isLoading()
                    && !largeRaid.isLastWave()) {
                setIdle();
                largeRaid.triggerNextWave();
                setActive();
            }
            for (Consumer<LargeRaid> task : tickIteratingTasks)
                task.accept(largeRaid);
        }

        for (Runnable task : tickTasks)
            task.run();
    }

    public Optional<LargeRaid> matchingLargeRaid(Location location) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(location)).findFirst();
    }

    public Optional<LargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

    public void registerTickTask(Runnable task) {
        tickTasks.add(task);
    }

    public void registerTickTask(Consumer<LargeRaid> task) {
        tickIteratingTasks.add(task);
    }

    public void unregisterTickTask(Runnable task) {
        tickTasks.remove(task);
    }

    public void unregisterTickTask(Consumer<LargeRaid> task) {
        tickIteratingTasks.remove(task);
    }

}