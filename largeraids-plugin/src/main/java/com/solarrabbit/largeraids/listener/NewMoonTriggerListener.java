package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class NewMoonTriggerListener extends TriggerListener {
    private int tickTaskId;

    public NewMoonTriggerListener(LargeRaids plugin) {
        super(plugin);
        tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> tick(), 0, 100);
    }

    @Override
    public void unregisterListener() {
        Bukkit.getScheduler().cancelTask(tickTaskId);
    }

    private void tick() {
        Bukkit.getWorlds().stream().filter(world -> isMidnight(world) && isNewMoon(world))
                .flatMap(world -> world.getPlayers().stream())
                .forEach(player -> triggerRaid(player, player.getLocation()));
    }

    private boolean isMidnight(World world) {
        long time = world.getTime();
        return time >= 18000 && time < 18100;
    }

    private boolean isNewMoon(World world) {
        return this.getMoonPhase(world) == 4;
    }

    private int getMoonPhase(World world) {
        int days = (int) world.getFullTime() / 24000;
        return days % 8;
    }

}
