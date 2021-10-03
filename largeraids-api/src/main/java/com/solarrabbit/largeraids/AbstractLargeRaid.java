package com.solarrabbit.largeraids;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import com.solarrabbit.largeraids.item.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class AbstractLargeRaid {
    private static final int RADIUS = 96;
    private static final int INNER_RADIUS = 64;
    protected final LargeRaids plugin;
    private final int maxTotalWaves;
    private int totalWaves;
    private int omenLevel;
    protected Location centre;
    protected Raid currentRaid;
    protected int currentWave;
    protected Set<UUID> pendingHeroes;
    protected boolean loading;
    protected Player player;

    public AbstractLargeRaid(LargeRaids plugin, Player player) {
        this.plugin = plugin;
        this.centre = player.getLocation(); // Not yet a real centre...
        this.player = player;
        this.maxTotalWaves = plugin.getConfig().getInt("raid.waves");
        this.currentWave = 1;
        this.pendingHeroes = new HashSet<>();

        this.totalWaves = this.maxTotalWaves;
        this.omenLevel = this.maxTotalWaves;
    }

    public AbstractLargeRaid(LargeRaids plugin, Player player, int omenLevel) {
        this(plugin, player);
        this.totalWaves = Math.max(5, omenLevel);
        this.omenLevel = omenLevel;
    }

    public boolean isSimilar(Raid raid) {
        return isSimilar(raid.getLocation());
    }

    public boolean isLoading() {
        return this.loading;
    }

    public void announceVictory() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.victory", null));
        if (sound != null)
            playSoundToPlayers(sound);

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        this.pendingHeroes.forEach(uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline)
                .ifPresent(player -> awardPlayer(player)));
    }

    public void announceDefeat() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.defeat", null));
        if (sound != null)
            playSoundToPlayers(sound);
    }

    public boolean isLastWave() {
        return this.currentWave == this.totalWaves;
    }

    protected boolean isSecondLastWave() {
        return this.totalWaves - this.currentWave == 1;
    }

    public int getCurrentWave() {
        return this.currentWave;
    }

    public int getTotalWaves() {
        return this.totalWaves;
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
     * @throws NullPointerException if the large raid has not attempted to start
     */
    public Location getCenter() {
        return this.currentRaid == null ? null : this.centre;
    }

    protected void setRaid(Raid raid) {
        this.currentRaid = raid;
        if (!this.plugin.getConfig().getBoolean("raid.trigger-is-centre"))
            this.centre = raid.getLocation();
    }

    protected void playSoundToPlayers(Sound sound) {
        getPlayersInRadius().forEach(player -> player.playSound(player.getLocation(), sound, 50, 1));
    }

    protected Location getWaveSpawnLocation() {
        List<Raider> list = this.currentRaid.getRaiders();
        return list.isEmpty() ? null : list.get(0).getLocation();
    }

    public Set<Player> getPlayersInRadius() {
        Collection<Entity> collection = this.centre.getWorld().getNearbyEntities(this.centre, RADIUS, RADIUS, RADIUS,
                entity -> entity instanceof Player
                        && centre.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    public Set<Player> getPlayersInInnerRadius() {
        Collection<Entity> collection = this.centre.getWorld().getNearbyEntities(this.centre, INNER_RADIUS,
                INNER_RADIUS, INNER_RADIUS, entity -> entity instanceof Player
                        && centre.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    protected void broadcastWave() {
        boolean title = this.plugin.getConfig().getBoolean("raid.announce-waves.title");
        boolean message = this.plugin.getConfig().getBoolean("raid.announce-waves.message");
        this.getPlayersInRadius().forEach(player -> {
            if (title) {
                String defaultStr = ChatColorUtil
                        .translate(this.plugin.getConfig().getString("wave-broadcast.title.default"));
                String finalStr = ChatColorUtil
                        .translate(this.plugin.getConfig().getString("wave-broadcast.title.final"));
                player.sendTitle(ChatColor.GOLD + (isLastWave() ? finalStr : String.format(defaultStr, currentWave)),
                        null, 10, 70, 20);
            }
            if (message) {
                String defaultStr = ChatColorUtil
                        .translate(this.plugin.getConfig().getString("wave-broadcast.message.default"));
                String finalStr = ChatColorUtil
                        .translate(this.plugin.getConfig().getString("wave-broadcast.message.final"));
                player.sendMessage(ChatColor.GOLD + (isLastWave() ? finalStr : String.format(defaultStr, currentWave)));
            }
        });
    }

    protected Sound getSound(String name) {
        return Stream.of(Sound.values()).filter(value -> value.name().equals(name)).findFirst().orElse(null);
    }

    private void awardPlayer(Player player) {
        player.sendMessage(ChatColorUtil.translate(this.plugin.getConfig().getString("receive-rewards")));

        ConfigurationSection conf = this.plugin.getConfig().getConfigurationSection("hero-of-the-village");
        int level = Math.min(conf.getInt("level"), this.omenLevel);
        int duration = conf.getInt("duration") * 60 * 20;
        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level - 1));

        ConfigurationSection itemAwards = this.plugin.getConfig().getConfigurationSection("rewards.items");
        ItemStack[] items = itemAwards.getKeys(false).stream()
                .map(itemConfig -> ItemCreator.getItemFromConfig(itemAwards.getConfigurationSection(itemConfig)))
                .toArray(ItemStack[]::new);
        player.getInventory().addItem(items)
                .forEach((i, item) -> player.getWorld().dropItem(player.getLocation(), item));

        this.plugin.getConfig().getStringList("rewards.commands").forEach(
                str -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("<player>", player.getName())));
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
        if (this.currentRaid.getBadOmenLevel() == 2)
            return false;
        this.currentRaid.setBadOmenLevel(2);
        return true;
    }

    public abstract void startRaid();

    public abstract void stopRaid();

    public abstract boolean isSimilar(Location location);

    public abstract void triggerNextWave();

    public abstract void clearHeroRecords();

    public abstract void spawnNextWave();

    public static int getDefaultWaveNumber(World world) {
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
