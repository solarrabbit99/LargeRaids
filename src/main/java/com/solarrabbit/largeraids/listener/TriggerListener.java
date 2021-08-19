package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class TriggerListener implements Listener {

    protected void triggerRaid(Location loc) {
        new LargeRaid(this.getPlugin(), loc).startRaid();
    }

    protected LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

}
