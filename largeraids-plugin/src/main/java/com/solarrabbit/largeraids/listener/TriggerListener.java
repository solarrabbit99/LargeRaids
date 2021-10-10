package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.AbstractLargeRaid;
import com.solarrabbit.largeraids.util.VersionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Player player) {
        AbstractLargeRaid largeRaid = VersionUtil.createLargeRaid(player);
        startRaid(largeRaid);
    }

    protected void triggerRaid(Player player, int omenLevel) {
        AbstractLargeRaid largeRaid = VersionUtil.createLargeRaid(player, omenLevel);
        startRaid(largeRaid);
    }

    private void startRaid(AbstractLargeRaid largeRaid) {
        largeRaid.startRaid();

        Location center = largeRaid.getCenter();
        if (center == null)
            return;

        if (!isAllowed(center))
            largeRaid.stopRaid();
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
        return this.getPlugin().getDatabaseAdapter().getCentres().values().stream()
                .anyMatch(location -> location.distanceSquared(loc) < 3);
    }

}
