package com.solarrabbit.largeraids;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.World;
import org.bukkit.entity.Raider;

public interface AbstractLargeRaid {

    public void startRaid();

    public void stopRaid();

    public boolean isSimilar(Raid raid);

    public boolean isSimilar(Location location);

    public boolean isLoading();

    public void triggerNextWave();

    public void spawnNextWave();

    public void announceVictory();

    public void announceDefeat();

    public List<Raider> getRemainingRaiders();

    public boolean isLastWave();

    public static int getDefaultWaveNumber(World world) {
        return 0;
    }

}
