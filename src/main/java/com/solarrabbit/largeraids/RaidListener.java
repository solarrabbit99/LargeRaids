package com.solarrabbit.largeraids;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class RaidListener implements Listener {
    public static final Set<LargeRaid> currentRaids = new HashSet<>();

    public static void addLargeRaid(LargeRaid raid) {
        currentRaids.add(raid);
    }

    public static void removeLargeRaid(LargeRaid raid) {
        currentRaids.remove(raid);
    }

    @EventHandler
    public void onTrigger(RaidTriggerEvent evt) {
        Raid raid = evt.getRaid();
        matchingLargeRaid(raid).ifPresent(largeRaid -> largeRaid.setRaid(raid));
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
        });
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent evt) {
        matchingLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            currentRaids.remove(largeRaid);
        });
    }

    @EventHandler
    public void onRaiderDeath(EntityDeathEvent evt) {
        if (RaiderConfig.valueOf(evt.getEntityType()) == null)
            return;
        for (LargeRaid largeRaid : currentRaids) {
            if (!largeRaid.isLoading() && largeRaid.getRemainingRaiders().isEmpty() && !largeRaid.isLastWave()) {
                largeRaid.triggerNextWave();
            }
            break;
        }
    }

    private Optional<LargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }
}