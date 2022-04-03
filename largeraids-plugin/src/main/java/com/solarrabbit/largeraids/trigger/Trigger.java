package com.solarrabbit.largeraids.trigger;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public abstract class Trigger implements Listener {
    protected final LargeRaids plugin;

    protected Trigger(LargeRaids plugin) {
        this.plugin = plugin;
    }

    protected void triggerRaid(CommandSender triggerer, Location location) {
        triggerRaid(triggerer, location, plugin.getRaidConfig().getMaximumWaves());
    }

    protected void triggerRaid(CommandSender triggerer, Location location, int omenLevel) {
        if (plugin.getRaidConfig().isAlwaysMaxWaves())
            omenLevel = plugin.getRaidConfig().getMaximumWaves();
        int maxRaids = plugin.getMiscConfig().getMaxRaid();
        if (maxRaids > 0 && plugin.getRaidManager().currentRaids.size() >= maxRaids)
            return;
        plugin.getTriggerManager().setLastTriggerer(triggerer);
        plugin.getRaidManager().createRaid(location, omenLevel);
    }

    public abstract void unregisterListener();

}
