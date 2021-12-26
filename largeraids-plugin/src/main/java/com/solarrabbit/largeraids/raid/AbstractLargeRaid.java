package com.solarrabbit.largeraids.raid;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.solarrabbit.largeraids.config.RaidConfig;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class AbstractLargeRaid {
    private static final int RADIUS = 96;
    private static final int INNER_RADIUS = 64;
    protected final RaidConfig config;
    private final int maxTotalWaves;
    private int totalWaves;
    private int omenLevel;
    protected Location center;
    protected Raid currentRaid;
    protected int currentWave;
    protected Set<UUID> pendingHeroes;
    protected boolean loading;
    protected Player player;

    public AbstractLargeRaid(RaidConfig config, Location location, int omenLevel) {
        this.config = config;
        this.center = location; // Not yet a real center...
        this.maxTotalWaves = config.getMaximumWaves();
        this.currentWave = 1;
        this.pendingHeroes = new HashSet<>();
        this.totalWaves = Math.max(5, omenLevel);
        this.omenLevel = omenLevel;
    }

    public boolean isSimilar(Raid raid) {
        if (this.currentRaid == null)
            return isSimilar(raid.getLocation());
        return this.currentRaid.getLocation().equals(raid.getLocation());
    }

    public boolean isLoading() {
        return loading;
    }

    public void announceStart() {
        // TODO: add configurable announcement
    }

    public void announceVictory() {
        Sound sound = config.getSounds().getVictorySound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        this.pendingHeroes.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline)
                .ifPresent(player -> awardPlayer(player)));
    }

    public void announceDefeat() {
        Sound sound = config.getSounds().getDefeatSound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public boolean isLastWave() {
        return currentWave == totalWaves;
    }

    public int getTotalRaidersAlive() {
        try {
            return this.currentRaid == null ? 0 : this.currentRaid.getRaiders().size();
        } catch (ConcurrentModificationException evt) {
            return 0;
        }
    }

    public void absorbOmenLevel(int level) {
        this.omenLevel = Math.min(this.maxTotalWaves, this.omenLevel + level);
        this.totalWaves = Math.max(5, omenLevel);
    }

    /**
     * Returns the actual center of the raid.
     *
     * @return {@code null} if the raid has stopped/failed to start
     */
    public Location getCenter() {
        return currentRaid == null ? null : center;
    }

    protected void setRaid(Raid raid) {
        currentRaid = raid;
        center = raid.getLocation();
    }

    protected void playSoundToPlayersInRadius(Sound sound) {
        getPlayersInRadius().forEach(player -> player.playSound(player.getLocation(), sound, 50, 1));
    }

    protected Location getWaveSpawnLocation() {
        List<Raider> list = this.currentRaid.getRaiders();
        return list.isEmpty() ? null : list.get(0).getLocation();
    }

    public Set<Player> getPlayersInRadius() {
        Collection<Entity> collection = center.getWorld().getNearbyEntities(center, RADIUS, RADIUS, RADIUS,
                entity -> entity instanceof Player
                        && center.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    public Set<Player> getPlayersInInnerRadius() {
        Collection<Entity> collection = center.getWorld().getNearbyEntities(center, INNER_RADIUS,
                INNER_RADIUS, INNER_RADIUS, entity -> entity instanceof Player
                        && center.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    protected void broadcastWave() {
        for (Player player : getPlayersInRadius()) {
            if (config.isTitleEnabled()) {
                String defaultStr = config.getDefaultWaveTitle(currentWave);
                String finalStr = config.getFinalWaveTitle();
                player.sendTitle(isLastWave() ? finalStr : defaultStr, null, 10, 70, 20);
            }
            if (config.isMessageEnabled()) {
                String defaultStr = config.getDefaultWaveMessage(currentWave);
                String finalStr = config.getFinalWaveMessage();
                player.sendMessage(isLastWave() ? finalStr : defaultStr);
            }
        }
    }

    private void awardPlayer(Player player) {
        int level = Math.min(config.getHeroLevel(), omenLevel);
        int duration = config.getHeroDuration() * 60 * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level - 1));

        player.sendMessage(config.getRewards().getMessage());
        player.getInventory().addItem(config.getRewards().getItems())
                .forEach((i, item) -> player.getWorld().dropItem(player.getLocation(), item));

        for (String command : config.getRewards().getCommands())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName()));
    }

    /**
     * Set the bad omen level of the current raid back to {@code 2} if it has been
     * increased by the absroption of player's omen. Used for detecting whether a
     * player with Bad Omen effect entered the raid.
     *
     * @return {@code true} if the omen level of the actual raid has been increased
     *         above 2
     */
    public boolean releaseOmen() {
        if (currentRaid.getBadOmenLevel() == 2)
            return false;
        currentRaid.setBadOmenLevel(2);
        return true;
    }

    public int getBadOmenLevel() {
        return omenLevel;
    }

    public abstract void startRaid();

    public abstract void stopRaid();

    public abstract boolean isSimilar(Location location);

    public abstract void triggerNextWave();

    /**
     * Spawns the raiders for the wave. This is a follow-up method of
     * {@link #triggerNextWave()}, and called to replace vanilla mobs spawns.
     */
    public abstract void spawnWave();

    protected int getDefaultWaveNumber(World world) {
        Difficulty difficulty = world.getDifficulty();
        switch (difficulty) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

}
