package com.solarrabbit.largeraids.listener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;

public class RaidListener implements Listener {
    private static final Set<AbstractLargeRaid> currentRaids = new HashSet<>();

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

    @EventHandler
    public void onRaiderDeath(EntityDeathEvent evt) {
        if (VersionUtil.getRaiderConfig(evt.getEntityType()) == null)
            return;
        for (AbstractLargeRaid largeRaid : currentRaids) {
            if (!largeRaid.isLoading() && largeRaid.getRemainingRaiders().isEmpty() && !largeRaid.isLastWave()) {
                largeRaid.triggerNextWave();
                break;
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