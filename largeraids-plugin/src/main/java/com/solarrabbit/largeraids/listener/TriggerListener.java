package com.solarrabbit.largeraids.listener;

import java.util.concurrent.CompletableFuture;
import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Location loc, Player player) {
        AbstractLargeRaid largeRaid = VersionUtil.createLargeRaid(loc, player);
        largeRaid.startRaid();
        isAllowed(largeRaid.getCentre()).whenComplete((bool, exp) -> {
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

    private CompletableFuture<Boolean> isAllowed(Location loc) {
        return getPlugin().getConfig().getBoolean("artificial-only") ? isInDatabase(loc)
                : CompletableFuture.completedFuture(true);
    }

    private CompletableFuture<Boolean> isInDatabase(Location loc) {
        return this.getPlugin().getDatabase().getCentres()
                .thenApply(map -> map.values().stream().anyMatch(location -> location.distanceSquared(loc) < 3));
    }

}
