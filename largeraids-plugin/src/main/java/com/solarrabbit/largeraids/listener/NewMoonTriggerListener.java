package com.solarrabbit.largeraids.listener;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class NewMoonTriggerListener extends TriggerListener {
    private JavaPlugin plugin;

    public NewMoonTriggerListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public int init() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> tick(), 0, 100);
    }

    public void tick() {
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
