package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public abstract class TriggerListener implements Listener {
    protected final LargeRaids plugin;

    protected TriggerListener(LargeRaids plugin) {
        this.plugin = plugin;
    }

    protected void triggerRaid(Location location) {
        triggerRaid(location, plugin.getRaidConfig().getMaximumWaves());
    }

    protected void triggerRaid(Location location, int omenLevel) {
        if (!isAllowed(location))
            return;
        LargeRaid largeRaid = new LargeRaid(plugin.getRaidConfig(), location, omenLevel);
        largeRaid.startRaid();
    }

    public abstract void unregisterListener();

    private boolean isAllowed(Location loc) {
        if (plugin.getTriggerConfig().isArtificialOnly())
            return isInDatabase(loc);
        return true;
    }

    private boolean isInDatabase(Location loc) {
        return plugin.getDatabaseAdapter().getCentres().values().stream()
                .anyMatch(location -> location.distanceSquared(loc) < Math.pow(64, 2));
    }

}
