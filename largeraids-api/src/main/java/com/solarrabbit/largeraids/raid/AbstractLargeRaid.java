package com.solarrabbit.largeraids.raid;

import java.util.Set;
import java.util.UUID;

import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public abstract class AbstractLargeRaid {
    protected LargeRaids plugin;
    protected Location centre;
    protected Raid currentRaid;
    protected int currentWave;
    protected Set<UUID> pendingHeroes;
    protected boolean loading;
    protected Player player;

    public AbstractLargeRaid(LargeRaids plugin, Player player) {
    }

    public AbstractLargeRaid(LargeRaids plugin, Player player, int omenLevel) {
    }

    public boolean isSimilar(Raid raid) {
        return false;
    }

    public boolean isLoading() {
        return false;
    }

    public void announceVictory() {
    }

    public void announceDefeat() {
    }

    public boolean isLastWave() {
        return false;
    }

    protected boolean isSecondLastWave() {
        return false;
    }

    public int getCurrentWave() {
        return 0;
    }

    public int getTotalWaves() {
        return 0;
    }

    public int getTotalRaidersAlive() {
        return 0;
    }

    public void absorbOmenLevel(int level) {
    }

    public Location getCenter() {
        return null;
    }

    protected void setRaid(Raid raid) {
    }

    protected void playSoundToPlayers(Sound sound) {
    }

    protected Location getWaveSpawnLocation() {
        return null;
    }

    public Set<Player> getPlayersInRadius() {
        return null;
    }

    public Set<Player> getPlayersInInnerRadius() {
        return null;
    }

    protected void broadcastWave() {
    }

    protected Sound getSound(String name) {
        return null;
    }

    public boolean releaseOmen() {
        return false;
    }

    public int getBadOmenLevel() {
        return 0;
    }

    public static int getDefaultWaveNumber(World world) {
        return 0;
    }

    public abstract void startRaid();

    public abstract void stopRaid();

    public abstract boolean isSimilar(Location location);

    public abstract void triggerNextWave();

    public abstract void clearHeroRecords();

    public abstract void spawnNextWave();

}
