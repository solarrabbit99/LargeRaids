package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Location loc, Player player) {
        VersionUtil.createLargeRaid(loc, player).startRaid();
    }

    protected LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

}
