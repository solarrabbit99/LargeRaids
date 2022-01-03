package com.solarrabbit.largeraids.raid;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractCraftWorldWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaiderWrapper;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidStopEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

public class RaidManager implements Listener {
    public final Set<LargeRaid> currentRaids = new HashSet<>();
    private final LargeRaids plugin;
    private boolean isIdle;

    public RaidManager(LargeRaids plugin) {
        this.plugin = plugin;
        isIdle = false;
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
        getLargeRaid(evt.getRaid()).ifPresent(largeRaid -> {
            setIdle();
            largeRaid.spawnWave();
            setActive();
        });
    }

    /**
     * Disables normal raid if enabled in configurations.
     *
     * @param evt raid triggering event
     */
    @EventHandler
    public void onNormalRaidTrigger(RaidTriggerEvent evt) {
        if (evt.getRaid().getBadOmenLevel() != 0) // Raid is getting extended
            return;
        if (isIdle()) // LargeRaid triggering
            return;
        if (!plugin.getTriggerConfig().canNormalRaid())
            evt.setCancelled(true);
    }

    @EventHandler
    public void onFinish(RaidFinishEvent evt) {
        Raid raid = evt.getRaid();
        getLargeRaid(raid).ifPresent(largeRaid -> {
            RaidStatus status = raid.getStatus();
            if (status == RaidStatus.VICTORY)
                largeRaid.announceVictory();
            else if (status == RaidStatus.LOSS)
                largeRaid.announceDefeat();
        });
    }

    @EventHandler
    public void onRaidStop(RaidStopEvent evt) {
        getLargeRaid(evt.getRaid()).ifPresent(largeRaid -> currentRaids.remove(largeRaid));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill(EntityDamageByEntityEvent evt) {
        if (evt.isCancelled())
            return;
        Entity killed = evt.getEntity();
        if (!(killed instanceof Raider))
            return;
        Raider raider = (Raider) killed;
        if (raider.getHealth() - evt.getFinalDamage() > 0)
            return;
        Player killer = raider.getKiller();
        if (killer == null)
            return;

        AbstractRaiderWrapper wrapper = VersionUtil.getCraftRaiderWrapper(raider).getHandle();
        AbstractRaidWrapper nmsRaid = wrapper.getCurrentRaid();
        if (nmsRaid.isEmpty())
            return;
        AbstractCraftRaidWrapper craftRaid = VersionUtil.getCraftRaidWrapper(nmsRaid);
        Optional<LargeRaid> lr = getLargeRaid(craftRaid.getRaid());
        lr.ifPresent(r -> r.incrementPlayerKill(killer));
    }

    public void init() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 1);
    }

    private void tick() {
        for (LargeRaid largeRaid : currentRaids)
            if (largeRaid.isActive() && largeRaid.getTotalRaidersAlive() == 0 && !largeRaid.isLoading()
                    && !largeRaid.isLastWave()) {
                setIdle();
                largeRaid.triggerNextWave();
                setActive();
            }
    }

    public Optional<LargeRaid> getLargeRaid(Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractCraftWorldWrapper craftWorld = VersionUtil.getCraftWorldWrapper(location.getWorld());
        AbstractRaidWrapper nmsRaid = craftWorld.getHandle().getRaidAt(blockPos);
        if (nmsRaid.isEmpty())
            return Optional.empty();
        AbstractCraftRaidWrapper craftRaid = VersionUtil.getCraftRaidWrapper(nmsRaid);
        return getLargeRaid(craftRaid.getRaid());
    }

    public Optional<LargeRaid> getLargeRaid(Raid raid) {
        return currentRaids.stream().filter(largeRaid -> largeRaid.isSimilar(raid)).findFirst();
    }

    public Optional<Raid> getRaid(Location location) {
        AbstractBlockPositionWrapper blockPos = VersionUtil.getBlockPositionWrapper(location);
        AbstractCraftWorldWrapper world = VersionUtil.getCraftWorldWrapper(location.getWorld());
        AbstractRaidWrapper raid = world.getHandle().getRaidAt(blockPos);
        return Optional.of(raid).filter(r -> !r.isEmpty()).map(VersionUtil::getCraftRaidWrapper)
                .map(AbstractCraftRaidWrapper::getRaid);
    }
}