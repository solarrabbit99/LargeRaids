package com.solarrabbit.largeraids.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class NewMoonTriggerListener extends TriggerListener {
    private final JavaPlugin plugin;
    private Integer tickTaskId;

    public NewMoonTriggerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.tickTaskId = null;
        init();
    }

    public int init() {
        this.tickTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> tick(), 0, 100);
        return this.tickTaskId;
    }

    @Override
    public void unregisterListener() {
        if (this.tickTaskId == null)
            return;
        Bukkit.getScheduler().cancelTask(this.tickTaskId);
    }

    private void tick() {
        Bukkit.getWorlds().stream().filter(world -> isMidnight(world) && isNewMoon(world))
                .flatMap(world -> world.getPlayers().stream())
                .forEach(player -> this.triggerRaid(player.getLocation(), player));
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
