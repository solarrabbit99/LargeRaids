package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.AbstractLargeRaid;
import com.solarrabbit.largeraids.raid.LargeRaid;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Location location) {
        triggerRaid(location, getPlugin().getConfig().getInt("raid.waves"));
    }

    protected void triggerRaid(Location location, int omenLevel) {
        if (!isAllowed(location))
            return;
        AbstractLargeRaid largeRaid = new LargeRaid(getPlugin().getRaidConfig(), location, omenLevel);
        largeRaid.startRaid();
    }

    protected LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

    public abstract void unregisterListener();

    private boolean isAllowed(Location loc) {
        if (getPlugin().getConfig().getBoolean("artificial-only"))
            return isInDatabase(loc);
        return true;
    }

    private boolean isInDatabase(Location loc) {
        return getPlugin().getDatabaseAdapter().getCentres().values().stream()
                .anyMatch(location -> location.distanceSquared(loc) < Math.pow(64, 2));
    }

}
