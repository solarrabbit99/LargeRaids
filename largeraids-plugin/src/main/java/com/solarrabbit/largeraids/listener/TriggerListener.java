package com.solarrabbit.largeraids.listener;

import java.util.concurrent.CompletableFuture;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.AbstractLargeRaid;
import com.solarrabbit.largeraids.util.VersionUtil;
import org.bukkit.Bukkit;
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

        isAllowed(center).whenComplete((bool, exp) -> {
            if (exp != null)
                throw new RuntimeException(exp);

            if (!bool)
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    largeRaid.stopRaid();
                });
        });
    }

    protected LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

    public abstract void unregisterListener();

    private CompletableFuture<Boolean> isAllowed(Location loc) {
        if (getPlugin().getConfig().getBoolean("artificial-only"))
            return isInDatabase(loc);
        return CompletableFuture.completedFuture(true);
    }

    private CompletableFuture<Boolean> isInDatabase(Location loc) {
        return this.getPlugin().getDatabase().getCentres()
                .thenApply(map -> map.values().stream().anyMatch(location -> location.distanceSquared(loc) < 3));
    }

}
