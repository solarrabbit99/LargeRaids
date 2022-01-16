package com.solarrabbit.largeraids.trigger;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class TimeBombTriggerListener extends Trigger {
    private int tickTaskId;

    public TimeBombTriggerListener(LargeRaids plugin) {
        super(plugin);
        tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> tick(), 0, 100);
    }

    @Override
    public void unregisterListener() {
        Bukkit.getScheduler().cancelTask(tickTaskId);
    }

    private void tick() {
        Bukkit.getWorlds().stream().filter(world -> isInTime(world))
                .flatMap(world -> world.getPlayers().stream())
                .forEach(player -> triggerRaid(player, player.getLocation()));
    }

    private boolean isInTime(World world) {
        int time = (int) world.getFullTime();
        return plugin.getTriggerConfig().getTimeBombConfig().getTicks().stream()
                .anyMatch(tick -> time >= tick && time < tick + 100);
    }

}
