package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Location loc, Player player) {
        AbstractLargeRaid largeRaid = VersionUtil.createLargeRaid(loc, player);
        largeRaid.startRaid();
        if (!isAllowed(largeRaid.getCentre()))
            largeRaid.stopRaid();
    }

    protected LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

    private boolean isAllowed(Location loc) {
        return getPlugin().getConfig().getBoolean("artificial-only") ? isInDatabase(loc) : true;
    }

    private boolean isInDatabase(Location loc) {
        return this.getPlugin().getDatabase().getCentres().values().stream()
                .anyMatch(location -> location.distanceSquared(loc) < 3);
    }

}
