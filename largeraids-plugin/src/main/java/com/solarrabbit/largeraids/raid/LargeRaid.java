package com.solarrabbit.largeraids.raid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.RaidConfig;
import com.solarrabbit.largeraids.config.RewardsConfig;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.raid.mob.RiderRaider;
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
    /**
     * Raiders will be invulnerable for 1 second to avoid damage from entity
     * cramming.
     */
    private static final int INVULNERABLE_TICKS = 20;
    private final RaidConfig raidConfig;
    private final RewardsConfig rewardsConfig;
    private final int maxTotalWaves;
    private final Location startLoc;
    private final Map<UUID, Integer> playerKills;
    private final Map<UUID, Double> playerDamage;
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
    LargeRaid(RaidConfig raidConfig, RewardsConfig rewardsConfig, Location location, int omenLevel) {
        this.raidConfig = raidConfig;
        this.rewardsConfig = rewardsConfig;
        startLoc = location;
        maxTotalWaves = raidConfig.getMaximumWaves();
        currentWave = 1;
        playerKills = new HashMap<>();
        playerDamage = new HashMap<>();
        this.omenLevel = Math.min(this.maxTotalWaves, omenLevel);
        totalWaves = Math.max(5, this.omenLevel);
    }

    /**
     * Kicks start the raid if there isn't one in progress in the location. The
     * first wave will be broadcasted to players within range with a summoning
     * sound. This method should always be called when {@link RaidManager} is
     * idle, and set back to active after calling the method.
     *
     * @return {@code true} if a new raid starts successfully
     */
    boolean startRaid() {
        if (!getNMSRaidAtCenter().isEmpty()) // There is an ongoing raid
            return false;

        AbstractRaidWrapper raid = createRaid(startLoc);
        if (raid.isEmpty()) // fail to create raid
            return false;

        setRaid(VersionUtil.getCraftRaidWrapper(raid).getRaid());

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            if (currentRaid.getStatus() == RaidStatus.STOPPED)
                return;
            broadcastWave();
            Sound sound = raidConfig.getSounds().getSummonSound();
            if (sound != null)
                playSoundToPlayersInRadius(sound);
        }, 2);

        return true;
    }

    /**
     * Triggers the next wave by updating the heros records and reassigning a new
     * vanilla raid. The wave will be broadcasted to players within range. This
     * method should always be called when {@link RaidManager} is idle, and
     * set back to active after calling the method.
     */
    void triggerNextWave() {
        currentWave++;
        broadcastWave();

        getCurrentNMSRaid().stop();
        AbstractRaidWrapper raid = createRaid(getCenter());
        if (raid.isEmpty()) // fail to create raid
            return;
        setRaid(VersionUtil.getCraftRaidWrapper(raid).getRaid());

        if (isLastWave())
            prepareLastWave();
    }

    /**
     * Spawns the raiders for the wave. This is a follow-up method of
     * {@link #triggerNextWave()}, and called to replace vanilla mobs spawns. This
     * method should always be called when {@link RaidManager} is idle, and
     * set back to active after calling the method.
     */
    void spawnWave() {
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

        AbstractRaidWrapper nmsRaid = getCurrentNMSRaid();

        List<Raider> newRaiders = new ArrayList<>();
        for (Map.Entry<Function<Location, ? extends com.solarrabbit.largeraids.raid.mob.Raider>, Integer> kv : raidConfig
                .getRaiders().getWaveMobs(this.currentWave).entrySet()) {
            for (int i = 0; i < kv.getValue(); i++) {
                com.solarrabbit.largeraids.raid.mob.Raider entity = kv.getKey().apply(loc);
                Raider bukkitEntity = (Raider) entity.getBukkitEntity();
                nmsRaid.joinRaid(2, VersionUtil.getCraftRaiderWrapper(bukkitEntity).getHandle(), null, true);
                bukkitEntity.setInvulnerable(true);
                newRaiders.add(bukkitEntity);
                if (entity instanceof RiderRaider) {
                    Raider ravager = (Raider) ((RiderRaider) entity).getVehicle();
                    nmsRaid.joinRaid(2, VersionUtil.getCraftRaiderWrapper(ravager).getHandle(), null, true);
                    ravager.setInvulnerable(true);
                    newRaiders.add(ravager);
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(LargeRaids.class), () -> {
            for (Raider raider : newRaiders)
                raider.setInvulnerable(false);
        }, INVULNERABLE_TICKS);

        raiders.forEach(raider -> {
            nmsRaid.removeFromRaid(VersionUtil.getCraftRaiderWrapper(raider).getHandle(), true);
            raider.remove();
        });
    }

    /**
     * Last wave is not skippable, use {@link #stopRaid()} instead. This
     * method should always be called when {@link RaidManager} is idle, and
     * set back to active after calling the method.
     */
    public void skipWave() {
        if (isLastWave())
            return;
        else if (!isLoading())
            for (Raider raider : currentRaid.getRaiders())
                raider.remove();
        triggerNextWave();
    }

    /**
     * Stops the ongoing raid.
     */
    public void stopRaid() {
        getCurrentNMSRaid().stop();
        for (Raider raider : currentRaid.getRaiders())
            raider.remove();
    }

    /**
     * Announces victory with a sound and award heros with configured rewards.
     */
    void announceVictory() {
        Sound sound = raidConfig.getSounds().getVictorySound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);
        getCurrentNMSRaid().getHeroesOfTheVillage().clear(); // prevent unintentional vanilla rewards
        for (UUID uuid : playerDamage.keySet())
            Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(this::shouldAwardPlayer).ifPresent(this::awardPlayer);
    }

    /**
     * Announces defeat with a sound.
     */
    void announceDefeat() {
        Sound sound = raidConfig.getSounds().getDefeatSound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);
    }

    /**
     * Returns the center of the raid.
     */
    public Location getCenter() {
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
        return getCurrentNMSRaid().isActive();
    }

    /**
     * Returns whether the raid is in the midst of loading a wave - a period of time
     * in between waves or when the raid just started.
     * 
     * @return {@code true} if wave is loading
     */
    public boolean isLoading() {
        AbstractRaidWrapper nmsRaid = getCurrentNMSRaid();
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
    void absorbOmenLevel(int level) {
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

    public Map<UUID, Integer> getPlayerKills() {
        return playerKills;
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

    void incrementPlayerKill(Player player) {
        playerKills.merge(player.getUniqueId(), 1, Integer::sum);
    }

    void incrementPlayerDamage(Player player, double damage) {
        playerDamage.merge(player.getUniqueId(), damage, Double::sum);
    }

    private void broadcastWave() {
        for (Player player : getPlayersInRadius()) {
            if (raidConfig.isTitleEnabled()) {
                String defaultStr = raidConfig.getDefaultWaveTitle(currentWave);
                String finalStr = raidConfig.getFinalWaveTitle();
                player.sendTitle(isLastWave() ? finalStr : defaultStr, null, 10, 70, 20);
            }
            if (raidConfig.isMessageEnabled()) {
                String defaultStr = raidConfig.getDefaultWaveMessage(currentWave);
                String finalStr = raidConfig.getFinalWaveMessage();
                player.sendMessage(isLastWave() ? finalStr : defaultStr);
            }
        }
    }

    private boolean shouldAwardPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        boolean hasMinKills = Optional.ofNullable(playerKills.get(uuid)).orElse(0).intValue() >= rewardsConfig
                .getMinRaiderKills();
        boolean hasMinDamage = Optional.ofNullable(playerDamage.get(uuid)).orElse(0.0).doubleValue() >= rewardsConfig
                .getMinDamageDeal();
        return hasMinKills && hasMinDamage;
    }

    private void awardPlayer(Player player) {
        int level = Math.min(rewardsConfig.getHeroLevel(), omenLevel);
        int duration = rewardsConfig.getHeroDuration() * 60 * 20;
        if (level > 0)
            player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level - 1));

        player.sendMessage(rewardsConfig.getMessage());
        player.getInventory().addItem(rewardsConfig.getItems())
                .forEach((i, item) -> player.getWorld().dropItem(player.getLocation(), item));

        for (String command : rewardsConfig.getCommands())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getName())
                    .replace("<omen>", String.valueOf(getBadOmenLevel())));
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
        getCurrentNMSRaid().setGroupsSpawned(withoutBonus);
    }

    private AbstractRaidWrapper getCurrentNMSRaid() {
        return currentRaid == null ? null : VersionUtil.getCraftRaidWrapper(currentRaid).getHandle();
    }

    private AbstractRaidWrapper getNMSRaidAtCenter() {
        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(getCenter());
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(getCenter().getWorld()).getHandle();
        return level.getRaidAt(blkPos);
    }

    /**
     * Creates a raid with a fake player entity at the given location. The raid's
     * bad omen is set to 2 arbitrarily. This method should always be called when
     * {@link RaidManager} is idle, and set back to active after calling the
     * method. This method may return empty wrapper if the raid is cancelled by
     * third party.
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
        if (!raid.isEmpty())
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
