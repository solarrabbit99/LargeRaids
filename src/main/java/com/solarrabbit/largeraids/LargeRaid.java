package com.solarrabbit.largeraids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.solarrabbit.largeraids.PluginLogger.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class LargeRaid {
    private static final int RADIUS = 96;
    // private static final double ENCHANTCHANCE = 0.75;
    private final LargeRaids plugin;
    private final int totalWaves;
    private Location centre;
    private Raid currentRaid;
    private int currentWave;
    private Set<UUID> pendingHeroes;

    public LargeRaid(LargeRaids plugin, Location location) {
        this.plugin = plugin;
        this.centre = location; // Not yet a real centre...
        this.totalWaves = plugin.getConfig().getInt("raid.waves");
        this.currentWave = 1;
        this.pendingHeroes = new HashSet<>();
    }

    public void startRaid() {
        if (this.centre.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            this.plugin.log(this.plugin.getMessage("difficulty.attempt-peaceful"), Level.WARN);
            return;
        }
        if (getNMSRaid() != null)
            return;

        Iterator<Player> iter = this.getPlayersInRadius().iterator();
        if (iter.hasNext()) {
            iter.next().addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 1, 5));
            RaidListener.addLargeRaid(this);
            this.broadcastWave();
        }
    }

    public void setRaid(Raid raid) {
        this.currentRaid = raid;
        this.centre = raid.getLocation();
    }

    public boolean isSimilar(Raid raid) {
        Location centre = raid.getLocation();
        BlockPos blkPos = new BlockPos(centre.getX(), centre.getY(), centre.getZ());
        ServerLevel level = ((CraftWorld) centre.getWorld()).getHandle();
        return level.getRaidAt(blkPos) == getNMSRaid();
    }

    public void triggerNextWave() {
        if (this.isLastWave())
            return;

        if (!needTrigger()) {
            this.currentWave++;
            this.broadcastWave();
            return;
        }

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        getNMSRaid().stop();

        Iterator<Player> iter = this.getPlayersInRadius().iterator();
        if (iter.hasNext()) {
            iter.next().addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, 1, 5));
            this.currentWave++;
            this.broadcastWave();
        }
    }

    public void spawnNextWave() {

    }

    public void awardHeroes() {
        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        ConfigurationSection conf = this.plugin.getConfig().getConfigurationSection("hero-of-the-village");
        int level = conf.getInt("level");
        int duration = conf.getInt("duration") * 60 * 20;
        this.pendingHeroes.forEach(
                uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline).ifPresent(player -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level));
                    // TODO spawn particles
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 100, 1);
                }));
    }

    public List<Raider> getRemainingRaiders() {
        return this.currentRaid == null ? new ArrayList<>() : this.currentRaid.getRaiders();
    }

    public boolean isLastWave() {
        return this.currentWave == this.totalWaves;
    }

    private Set<Player> getPlayersInRadius() {
        Collection<Entity> collection = this.centre.getWorld().getNearbyEntities(this.centre, RADIUS, RADIUS, RADIUS,
                entity -> entity instanceof Player
                        && centre.distanceSquared(entity.getLocation()) <= Math.pow(RADIUS, 2));
        Set<Player> set = new HashSet<>();
        collection.forEach(player -> set.add((Player) player));
        return set;
    }

    private net.minecraft.world.entity.raid.Raid getNMSRaid() {
        BlockPos blkPos = new BlockPos(centre.getX(), centre.getY(), centre.getZ());
        ServerLevel level = ((CraftWorld) centre.getWorld()).getHandle();
        return level.getRaidAt(blkPos);
    }

    private void broadcastWave() {
        this.getPlayersInRadius().forEach(player -> player
                .sendTitle(ChatColor.GOLD + (isLastWave() ? "Final Wave" : "Wave " + currentWave), null, 10, 70, 20));
    }

    private boolean needTrigger() {
        return this.totalWaves - this.currentWave >= (getDefaultWaveNumber(this.centre.getWorld()) + 1);
    }

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
