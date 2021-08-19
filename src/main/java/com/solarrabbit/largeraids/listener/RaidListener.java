package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.solarrabbit.largeraids.LargeRaid;
import com.solarrabbit.largeraids.RaiderConfig;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;

public class RaidListener implements Listener {
    public static final Set<LargeRaid> currentRaids = new HashSet<>();

    public static void addLargeRaid(LargeRaid raid) {
        currentRaids.add(raid);
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