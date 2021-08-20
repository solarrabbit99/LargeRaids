package com.solarrabbit.largeraids.v1_17;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_17_R1.CraftRaid;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftRaider;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.saveddata.SavedData;

public class LargeRaid implements AbstractLargeRaid {
    private static final int RADIUS = 96;
    private final LargeRaids plugin;
    private final int totalWaves;
    private Location centre;
    private Raid currentRaid;
    private int currentWave;
    private Set<UUID> pendingHeroes;
    private boolean loading;

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

        triggerRaid(this.centre);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (this.currentRaid.getStatus() == RaidStatus.ONGOING) {
                RaidListener.addLargeRaid(this);
                this.broadcastWave();
                Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.summon", null));
                if (sound != null)
                    playSoundToPlayers(sound);
            }
        }, 2);
    }

    public void stopRaid() {
        this.getNMSRaid().stop();
        RaidListener.removeLargeRaid(this);
    }

    public boolean isSimilar(Raid raid) {
        return isSimilar(raid.getLocation());
    }

    public boolean isSimilar(Location location) {
        BlockPos blkPos = new BlockPos(location.getX(), location.getY(), location.getZ());
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        return level.getRaidAt(blkPos) == getNMSRaid();
    }

    public boolean isLoading() {
        return this.loading;
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

        triggerRaid(this.centre);
        this.currentWave++;
        this.broadcastWave();
    }

    public void spawnNextWave() {
        List<Raider> raiders = this.currentRaid.getRaiders();
        Location loc = this.getWaveSpawnLocation();
        net.minecraft.world.entity.raid.Raid nmsRaid = getNMSRaid();

        for (RaiderConfig raider : RaiderConfig.values()) {
            for (int i = 0; i < raider.getSpawnNumber(this.currentWave); i++) {
                net.minecraft.world.entity.raid.Raider mob = raider.getNMSType()
                        .create(((CraftWorld) centre.getWorld()).getHandle());
                nmsRaid.joinRaid(getDefaultWaveNumber(this.centre.getWorld()), mob,
                        new BlockPos(loc.getX(), loc.getY(), loc.getZ()), false);
            }
        }

        raiders.forEach(raider -> {
            nmsRaid.removeFromRaid(((CraftRaider) raider).getHandle(), true);
            raider.remove();
        });

        this.loading = false;
    }

    public void announceVictory() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.victory", null));
        if (sound != null)
            playSoundToPlayers(sound);

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        ConfigurationSection conf = this.plugin.getConfig().getConfigurationSection("hero-of-the-village");
        int level = conf.getInt("level");
        int duration = conf.getInt("duration") * 60 * 20;
        this.pendingHeroes.forEach(
                uuid -> Optional.ofNullable(Bukkit.getPlayer(uuid)).filter(Player::isOnline).ifPresent(player -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, duration, level));
                }));
    }

    public void announceDefeat() {
        Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.defeat", null));
        if (sound != null)
            playSoundToPlayers(sound);
    }

    public List<Raider> getRemainingRaiders() {
        return this.currentRaid == null ? new ArrayList<>() : this.currentRaid.getRaiders();
    }

    public boolean isLastWave() {
        return this.currentWave == this.totalWaves;
    }

    private void setRaid(Raid raid) {
        this.currentRaid = raid;
        this.centre = raid.getLocation();
        this.loading = true;
    }

    private void playSoundToPlayers(Sound sound) {
        getPlayersInRadius().forEach(player -> player.playSound(player.getLocation(), sound, 50, 1));
    }

    private Location getWaveSpawnLocation() {
        List<Raider> list = this.currentRaid.getRaiders();
        return list.isEmpty() ? null : list.get(0).getLocation();
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

    private void triggerRaid(Location location) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "LargeRaids");
        net.minecraft.server.level.ServerPlayer abstractPlayer = new net.minecraft.server.level.ServerPlayer(nmsServer,
                nmsWorld, profile);
        ((net.minecraft.world.entity.Entity) abstractPlayer).setPos(location.getX(), location.getY(), location.getZ());

        Raids raids = nmsWorld.getRaids();
        try {
            raids.createOrExtendRaid(abstractPlayer);
        } catch (NullPointerException e) {
            // Exception caused by failure to send packets to non-existing npc
        }
        ((SavedData) raids).setDirty();

        net.minecraft.world.entity.raid.Raid raid = this.getNMSRaid();
        raid.setBadOmenLevel(5);
        this.setRaid(new CraftRaid(raid));
    }

    private void broadcastWave() {
        boolean title = this.plugin.getConfig().getBoolean("raid.announce-waves.title");
        boolean message = this.plugin.getConfig().getBoolean("raid.announce-waves.message");
        this.getPlayersInRadius().forEach(player -> {
            if (title)
                player.sendTitle(ChatColor.GOLD + (isLastWave() ? "Final Wave" : "Wave " + currentWave), null, 10, 70,
                        20);
            if (message)
                player.sendMessage(
                        ChatColor.GOLD + "Spawning " + (isLastWave() ? "final wave" : "wave " + currentWave) + "...");
        });
    }

    private Sound getSound(String name) {
        return Stream.of(Sound.values()).filter(value -> value.name().equals(name)).findFirst().orElse(null);
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
