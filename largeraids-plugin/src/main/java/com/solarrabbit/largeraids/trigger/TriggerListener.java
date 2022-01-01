package com.solarrabbit.largeraids.trigger;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.RaidManager;
import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public abstract class TriggerListener implements Listener {
    protected final LargeRaids plugin;

    protected TriggerListener(LargeRaids plugin) {
        this.plugin = plugin;
    }

    protected void triggerRaid(CommandSender triggerer, Location location) {
        triggerRaid(triggerer, location, plugin.getRaidConfig().getMaximumWaves());
    }

    protected void triggerRaid(CommandSender triggerer, Location location, int omenLevel) {
        String broadcastMessage = null;
        if (plugin.getTriggerConfig().isArtificialOnly()) {
            String centerName = getArtificialVillageCenterName(location);
            if (centerName == null)
                return;
            broadcastMessage = plugin.getTriggerConfig().getBroadcastMessage(triggerer, centerName);
        }

        LargeRaid largeRaid = new LargeRaid(plugin.getRaidConfig(), location, omenLevel);
        RaidManager listener = plugin.getRaidManager();
        if (listener.getLargeRaid(location).isPresent())
            return;
        listener.setIdle();
        if (largeRaid.startRaid()) {
            listener.currentRaids.add(largeRaid);
            if (broadcastMessage != null)
                Bukkit.broadcastMessage(broadcastMessage);
        }
        listener.setActive();
    }

    public abstract void unregisterListener();

    @Nullable
    private String getArtificialVillageCenterName(Location location) {
        return plugin.getDatabaseAdapter().getCentres().entrySet().stream()
                .filter(entry -> entry.getValue().distanceSquared(location) < Math.pow(64, 2))
                .map(entry -> entry.getKey())
                .findFirst().orElse(null);
    }

}
