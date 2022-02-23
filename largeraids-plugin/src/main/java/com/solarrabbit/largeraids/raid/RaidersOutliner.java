package com.solarrabbit.largeraids.raid;

import java.util.List;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.entity.Raider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class RaidersOutliner {
    /** Default duration for glowing effect (in ticks). */
    private static final int DEFAULT_GLOW_TICK = 60;
    /** Offset from ringing of bell to application of effect (in ticks). */
    private static final int OFFSET_TICK = 45;
    /** Maximum distance from bell in which raiders would have been affected. */
    private static final int INNER_RADIUS = 32;

    /**
     * Outlines all raiders within the raid after a default number of ticks that is
     * used in vanilla after resonate sound plays.
     *
     * @param raid     that the raiders to be outlined are in
     * @param duration in ticks
     */
    protected void outlineAllRaiders(Raid raid, int duration) {
        List<Raider> raiders = raid.getRaiders();
        if (raiders.isEmpty())
            return;
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, duration, 0);
        JavaPlugin plugin = JavaPlugin.getPlugin(LargeRaids.class);
        Bukkit.getScheduler().runTaskLater(plugin, () -> raiders.forEach(raider -> raider.addPotionEffect(effect)),
                OFFSET_TICK);
    }

    protected void outlineAllRaiders(Raid raid) {
        outlineAllRaiders(raid, DEFAULT_GLOW_TICK);
    }

    /**
     * Derived classes should check that {@link #anyRaidersInRange(Raid, Location)}
     * returns {@code true} before calling this method to prevent double resonates.
     * 
     * @param source location of the bell/source
     */
    protected void resonateBell(Location source) {
        source.getWorld().playSound(source, Sound.BLOCK_BELL_RESONATE, 1, 1);
    }

    protected boolean anyRaidersInRange(Raid raid, Location source) {
        List<Raider> raiders = raid.getRaiders();
        return raiders.isEmpty() ? false
                : raiders.stream()
                        .anyMatch(raider -> raider.getLocation().distanceSquared(source) < Math.pow(INNER_RADIUS, 2));
    }
}
