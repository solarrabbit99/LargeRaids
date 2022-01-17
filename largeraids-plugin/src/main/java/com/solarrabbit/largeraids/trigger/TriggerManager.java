package com.solarrabbit.largeraids.trigger;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.event.LargeRaidTriggerEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TriggerManager implements Listener {
    private final LargeRaids plugin;
    private CommandSender lastTriggerer;

    public TriggerManager(LargeRaids plugin) {
        this.plugin = plugin;
    }

    public void setLastTriggerer(CommandSender triggerer) {
        this.lastTriggerer = triggerer;
    }

    @EventHandler
    public void onLargeRaidTrigger(LargeRaidTriggerEvent evt) {
        if (plugin.getTriggerConfig().isArtificialOnly()) {
            String centerName = getCenterName(evt.getLargeRaid().getCenter());
            if (centerName == null) {
                evt.setCancelled(true);
                return;
            }
            Bukkit.broadcastMessage(plugin.getTriggerConfig().getBroadcastMessage(lastTriggerer, centerName));
        }
    }

    @Nullable
    private String getCenterName(Location location) {
        return plugin.getDatabaseAdapter().getCentres().entrySet().stream()
                .filter(entry -> location.getWorld().equals(entry.getValue().getWorld()))
                .filter(entry -> entry.getValue().distanceSquared(location) < Math.pow(64, 2))
                .map(Entry::getKey)
                .findFirst().orElse(null);
    }
}
