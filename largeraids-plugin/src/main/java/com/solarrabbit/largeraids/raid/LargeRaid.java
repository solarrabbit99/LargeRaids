package com.solarrabbit.largeraids.raid;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.RaidConfig;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LargeRaid {
    private static final int RADIUS = 96;
    private static final int VANILLA_RAID_OMEN_LEVEL = 2;
    private final RaidConfig config;
    private final int maxTotalWaves;
    private final Location startLoc;
    private final Set<UUID> pendingHeroes;
    private int totalWaves;
    private int omenLevel;
    private Raid currentRaid;
    private int currentWave;

    /**
     * Constructs a large raid object.
     *
     * @param config    configurations that the large raid should follow
     * @param location  location at which the large raid should be triggered
     * @param omenLevel the starting omen level of the large raid
     */
    public LargeRaid(RaidConfig config, Location location, int omenLevel) {
        this.config = config;
        startLoc = location;
        maxTotalWaves = config.getMaximumWaves();
        currentWave = 1;
        pendingHeroes = new HashSet<>();
        totalWaves = Math.max(5, omenLevel);
        this.omenLevel = omenLevel;
    }

    /**
     * Kicks start the raid if there isn't one in progress in the location. The
     * first wave will be broadcasted to players within range with a summoning
     * sound. This method should always be called when {@link BukkitRaidListener} is
     * idle, and set back to active after calling the method.
     *
     * @return {@code true} if a new raid starts successfully
     */
    public boolean startRaid() {
        if (!getNMSRaid().isEmpty()) // There is an ongoing raid
            return false;

        AbstractRaidWrapper raid = createRaid(startLoc);
        if (raid.isEmpty()) // fail to create raid
            return false;

        setRaid(VersionUtil.getCraftRaidWrapper(raid).getRaid());

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            if (currentRaid.getStatus() == RaidStatus.STOPPED)
                return;
            broadcastWave();
            Sound sound = config.getSounds().getSummonSound();
            if (sound != null)
                playSoundToPlayersInRadius(sound);
        }, 2);

        return true;
    }

    /**
     * Triggers the next wave by updating the heros records and reassigning a new
     * vanilla raid. The wave will be broadcasted to players within range. This
     * method should always be called when {@link BukkitRaidListener} is idle, and
     * set back to active after calling the method.
     */
    public void triggerNextWave() {
        transferHeroRecords();

        currentWave++;
        broadcastWave();

        getNMSRaid().stop();
        setRaid(VersionUtil.getCraftRaidWrapper(createRaid(getCenter())).getRaid());

        if (isLastWave())
            prepareLastWave();
    }

    /**
     * Spawns the raiders for the wave. This is a follow-up method of
     * {@link #triggerNextWave()}, and called to replace vanilla mobs spawns. This
     * method should always be called when {@link BukkitRaidListener} is idle, and
     * set
     * set back to active after calling the method.
     */
    public void spawnWave() {
        List<Raider> raiders = currentRaid.getRaiders();
        Location loc = getWaveSpawnLocation();

        // happens when spawned mobs are too far from the village
        if (getCenter().distanceSquared(loc) >= Math.pow(RADIUS, 2)) {
            for (Raider raider : raiders)
                raider.remove();
            if (!isLastWave())
                triggerNextWave();
            return;
        }

        AbstractRaidWrapper nmsRaid = getNMSRaid();

        for (EventRaider raider : config.getRaiders().getWaveMobs(this.currentWave)) {
            Raider entity = raider.spawn(loc);
            nmsRaid.joinRaid(2, VersionUtil.getCraftRaiderWrapper(entity).getHandle(), null, true);
        }

        raiders.forEach(raider -> {
            nmsRaid.removeFromRaid(VersionUtil.getCraftRaiderWrapper(raider).getHandle(), true);
            raider.remove();
        });
    }

    /**
     * Stops the ongoing raid.
     */
    public void stopRaid() {
        AbstractRaidWrapper raid = getNMSRaid();
        if (!raid.isEmpty())
            raid.stop();
    }

    /**
     * Announces victory with a sound and award heros with configured rewards.
     */
    public void announceVictory() {
        Sound sound = config.getSounds().getVictorySound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);

        transferHeroRecords();
        this.pendingHeroes.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline)
                .ifPresent(player -> awardPlayer(player)));
    }

    /**
     * Announces defeat with a sound.
     */
    public void announceDefeat() {
        Sound sound = config.getSounds().getDefeatSound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);
    }

    /**
     * Returns the center of the raid.
     */
    private Location getCenter() {
        return currentRaid == null ? startLoc : currentRaid.getLocation();
    }

    /**
     * Returns the omen level of the large raid.
     *
     * @return large raid's omen level
     */
    public int getBadOmenLevel() {
        return omenLevel;
    }

    /**
     * Returns the wave that the large raid is currently at.
     *
     * @return current wave of the large raid
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Returns the total waves that the large raid current has. Total waves may
     * change according to its omen levels.
     *
     * @return current total waves
     */
    public int getTotalWaves() {
        return totalWaves;
    }

    /**
     * Returns whether the large raid is at its last wave.
     *
     * @return {@code true} if the large raid has reached its last wave
     */
    public boolean isLastWave() {
        return currentWave == totalWaves;
    }

    public boolean isActive() {
        return !getNMSRaid().isEmpty();
    }

    /**
     * Returns whether the raid is in the midst of loading a wave - a period of time
     * in between waves or when the raid just started.
     * 
     * @return {@code true} if wave is loading
     */
    public boolean isLoading() {
        AbstractRaidWrapper nmsRaid = getNMSRaid();
        return !nmsRaid.hasFirstWaveSpawned() || nmsRaid.isBetweenWaves();
    }

    /**
     * Returns the number of raiders in the raid who are alive.
     *
     * @return number of alive raiders
     */
    public int getTotalRaidersAlive() {
        try {
            return currentRaid == null ? 0 : currentRaid.getRaiders().size();
        } catch (ConcurrentModificationException evt) {
            return 0;
        }
    }

    /**
     * Absorbs a certain level of bad omen, which may change the overall omen level
     * and total waves of the large raid.
     *
     * @param level levels to absorb
     */
    public void absorbOmenLevel(int level) {
        omenLevel = Math.min(this.maxTotalWaves, this.omenLevel + level);
        totalWaves = Math.max(5, omenLevel);
    }

    public Set<Player> getPlayersInRadius() {
        Collection<Entity> collection = getCenter().getWorld().getNearbyEntities(getCenter(), RADIUS, RADIUS, RADIUS,
                entity -> entity instanceof Player
                        && getCenter().distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    /**
     * Set the bad omen level of the current raid back to {@code 2} if it has been
     * increased by the absorption of player's omen. Used for detecting whether a
     * player with Bad Omen effect entered the raid.
     *
     * @return {@code true} if the omen level of the actual raid has been increased
     *         above 2
     */
    public boolean releaseOmen() {
        if (currentRaid.getBadOmenLevel() <= VANILLA_RAID_OMEN_LEVEL)
            return false;
        currentRaid.setBadOmenLevel(VANILLA_RAID_OMEN_LEVEL);
        return true;
    }

    public boolean isSimilar(Raid raid) {
        Objects.requireNonNull(currentRaid);
        return this.currentRaid.getLocation().equals(raid.getLocation());
    }

    /**
     * Serves as a precaution for player to be awarded with vanilla rewards
     * unintentionally.
     */
    private void transferHeroRecords() {
        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        getNMSRaid().getHeroesOfTheVillage().clear();
    }

    private void broadcastWave() {
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

    private void setRaid(Raid raid) {
        currentRaid = raid;
    }

    private void playSoundToPlayersInRadius(Sound sound) {
        getPlayersInRadius().forEach(player -> player.playSound(player.getLocation(), sound, 50, 1));
    }

    private Location getWaveSpawnLocation() {
        List<Raider> list = this.currentRaid.getRaiders();
        return list.isEmpty() ? null : list.get(0).getLocation();
    }

    private void prepareLastWave() {
        int withoutBonus = getDefaultWaveNumber(getCenter().getWorld());
        getNMSRaid().setGroupsSpawned(withoutBonus);
    }

    private AbstractRaidWrapper getNMSRaid() {
        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(getCenter());
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(getCenter().getWorld()).getHandle();
        return level.getRaidAt(blkPos);
    }

    /**
     * Creates a raid with a fake player entity at the given location. The raid's
     * bad omen is set to 2 arbitrarily. This method should always be called when
     * {@link BukkitRaidListener} is idle, and set back to active after calling the
     * method.
     *
     * @param location to create the raid
     * @return wrapped NMS raid created
     */
    private AbstractRaidWrapper createRaid(Location location) {
        AbstractPlayerEntityWrapper abstractPlayer = createEntityPlayer(location);
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(location.getWorld()).getHandle();
        AbstractRaidsWrapper raids = level.getRaids();
        try {
            raids.createOrExtendRaid(abstractPlayer);
        } catch (NullPointerException e) {
            // Exception caused by failure to send packets to non-existing npc
        }
        raids.setDirty();

        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(location.getX(), location.getY(),
                location.getZ());
        AbstractRaidWrapper raid = level.getRaidAt(blkPos);
        raid.setBadOmenLevel(VANILLA_RAID_OMEN_LEVEL);
        return raid;
    }

    /**
     * Creates a fake entity player at the given location.
     *
     * @param location for the entity to be set at
     * @return wrapped player entity
     */
    private AbstractPlayerEntityWrapper createEntityPlayer(Location location) {
        AbstractMinecraftServerWrapper nmsServer = VersionUtil.getCraftServerWrapper(Bukkit.getServer()).getServer();
        AbstractWorldServerWrapper nmsWorld = VersionUtil.getCraftWorldWrapper(location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "LargeRaids");
        AbstractPlayerEntityWrapper abstractPlayer = VersionUtil.getPlayerEntityWrapper(nmsServer,
                nmsWorld, profile);
        abstractPlayer.setPosition(location.getX(), location.getY(), location.getZ());
        return abstractPlayer;
    }

    private int getDefaultWaveNumber(World world) {
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
