package com.solarrabbit.largeraids.raid;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.config.RaidConfig;
import com.solarrabbit.largeraids.listener.RaidListener;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Raider;

public class LargeRaid extends AbstractLargeRaid {

    public LargeRaid(RaidConfig config, Location location, int omenLevel) {
        super(config, location, omenLevel);
    }

    @Override
    public void startRaid() {
        if (!getNMSRaid().isEmpty()) // There is an ongoing raid.
            return;

        RaidListener.addLargeRaid(this); // Register itself as large raid before RaidTriggerEvent
        AbstractRaidWrapper raid = createRaid(center);
        if (raid.isEmpty()) { // fail to create raid
            RaidListener.removeLargeRaid(this);
            return;
        }

        setRaid(VersionUtil.getCraftRaidWrapper(raid).getRaid());
        loading = true; // IMPORTANT, else next wave will be triggered.

        broadcastWave();
        announceStart();
        Sound sound = config.getSounds().getSummonSound();
        if (sound != null)
            playSoundToPlayersInRadius(sound);
    }

    @Override
    public void stopRaid() {
        AbstractRaidWrapper raid = this.getNMSRaid();
        if (!raid.isEmpty())
            raid.stop();
        RaidListener.removeLargeRaid(this);
    }

    @Override
    public boolean isSimilar(Location location) {
        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(location.getX(), location.getY(),
                location.getZ());
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(location.getWorld()).getHandle();
        return level.getRaidAt(blkPos).equals(getNMSRaid());
    }

    @Override
    public void triggerNextWave() {
        transferHeroRecords();

        loading = true;
        currentWave++;
        broadcastWave();

        getNMSRaid().stop();
        setRaid(VersionUtil.getCraftRaidWrapper(createRaid(center)).getRaid());

        if (isLastWave())
            prepareLastWave();
    }

    /**
     * Serves as a precaution for player to be awarded with vanilla rewards
     * unintentionally.
     */
    private void transferHeroRecords() {
        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        getNMSRaid().getHeroesOfTheVillage().clear();
    }

    @Override
    public void spawnWave() {
        List<Raider> raiders = this.currentRaid.getRaiders();

        // happens when spawned mobs are too far from the village
        if (!this.loading) {
            for (Raider raider : raiders)
                raider.remove();
            triggerNextWave();
            return;
        }

        Location loc = getWaveSpawnLocation();
        AbstractRaidWrapper nmsRaid = getNMSRaid();

        for (EventRaider raider : config.getRaiders().getWaveMobs(this.currentWave)) {
            Raider entity = raider.spawn(loc);
            nmsRaid.joinRaid(2, VersionUtil.getCraftRaiderWrapper(entity).getHandle(), null, true);
        }

        raiders.forEach(raider -> {
            nmsRaid.removeFromRaid(VersionUtil.getCraftRaiderWrapper(raider).getHandle(), true);
            raider.remove();
        });

        loading = false;
    }

    private void prepareLastWave() {
        int secondLast = getDefaultWaveNumber(this.center.getWorld());
        getNMSRaid().setGroupsSpawned(secondLast);
    }

    private AbstractRaidWrapper getNMSRaid() {
        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(center);
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(center.getWorld()).getHandle();
        return level.getRaidAt(blkPos);
    }

    /**
     * Creates a raid with a fake player entity at the given location. The raid's
     * bad omen is set to 2 arbitrarily.
     *
     * @param location to create the raid
     * @return wrapped NMS raid created
     */
    private AbstractRaidWrapper createRaid(Location location) {
        AbstractPlayerEntityWrapper abstractPlayer = createEntityPlayer(location);
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(center.getWorld()).getHandle();
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
        raid.setBadOmenLevel(2);
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

}
