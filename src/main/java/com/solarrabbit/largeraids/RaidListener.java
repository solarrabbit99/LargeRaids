package com.solarrabbit.largeraids;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.solarrabbit.largeraids.PluginLogger.Level;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.raid.RaidStopEvent.Reason;

public class RaidListener implements Listener {
    public static final Set<LargeRaid> currentRaids = new HashSet<>();
    private LargeRaids plugin;

    public RaidListener(LargeRaids plugin) {
        this.plugin = plugin;
    }

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
            currentRaids.remove(largeRaid);
        });
    }

    @EventHandler
    public void onPeaceful(RaidStopEvent evt) {
        if (evt.getReason() == Reason.PEACE) {
            this.plugin.log(this.plugin.getMessage("difficulty.ongoing-peaceful"), Level.WARN);
            currentRaids.clear();
        }
    }

    @EventHandler
    public void onRaiderDeath(EntityDeathEvent evt) {
        if (RaiderConfig.valueOf(evt.getEntityType()) == null)
            return;
        for (LargeRaid largeRaid : currentRaids) {
            if (largeRaid.getRemainingRaiders().isEmpty() && !largeRaid.isLastWave()) {
                largeRaid.triggerNextWave();
            }
            break;
        }
    }

    private Optional<LargeRaid> matchingLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }
}