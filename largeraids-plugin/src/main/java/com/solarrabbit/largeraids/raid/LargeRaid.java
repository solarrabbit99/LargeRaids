package com.solarrabbit.largeraids.raid;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.listener.RaidListener;
import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;
import com.solarrabbit.largeraids.raid.mob.EventRaider;
import com.solarrabbit.largeraids.util.ChatColorUtil;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;

public class LargeRaid extends AbstractLargeRaid {

    public LargeRaid(LargeRaids plugin, Player player) {
        super(plugin, player);
    }

    public LargeRaid(LargeRaids plugin, Player player, int omenLevel) {
        super(plugin, player, omenLevel);
    }

    @Override
    public void startRaid() {
        if (this.centre.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            String peacefulMessage = ChatColorUtil.translate(this.plugin.getConfig().getString("attempt-peaceful"));
            this.plugin.log(peacefulMessage, Level.WARN);
            if (this.player != null)
                player.sendMessage(ChatColor.YELLOW + peacefulMessage);
            return;
        }
        if (!getNMSRaid().isEmpty())
            return;

        RaidListener.addLargeRaid(this); // Register itself as large raid before RaidTriggerEvent
        triggerRaid(this.centre);
        this.loading = true; // IMPORTANT, else next wave will be triggered.

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (this.currentRaid.getStatus() == RaidStatus.ONGOING) {
                this.broadcastWave();
                Sound sound = getSound(this.plugin.getConfig().getString("raid.sounds.summon", null));
                if (sound != null)
                    playSoundToPlayers(sound);
            } else {
                RaidListener.removeLargeRaid(this);
            }
        }, 2);
    }

    @Override
    public void stopRaid() {
        AbstractRaidWrapper raid = this.getNMSRaid();
        if (raid != null)
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
        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        if (this.isLastWave()) {
            clearHeroRecords();
            return;
        }

        getNMSRaid().stop();

        this.loading = true;
        this.currentWave++;
        this.broadcastWave();
        triggerRaid(this.centre);

        if (isSecondLastWave())
            this.setLastWave();
    }

    @Override
    public void clearHeroRecords() {
        getNMSRaid().getHeroesOfTheVillage().clear();
    }

    @Override
    public void spawnNextWave() {
        List<Raider> raiders = this.currentRaid.getRaiders();

        if (!this.loading) {
            for (Raider raider : raiders)
                raider.remove();
            triggerNextWave();
            return;
        }

        Location loc = this.getWaveSpawnLocation();
        AbstractRaidWrapper nmsRaid = getNMSRaid();

        for (EventRaider raider : plugin.getRaiderConfig().getWaveMobs(this.currentWave)) {
            Raider entity = raider.spawn(loc);
            nmsRaid.addWaveMob(nmsRaid.getGroupsSpawned(), VersionUtil.getCraftRaiderWrapper(entity).getHandle(),
                    false);
        }

        raiders.forEach(raider -> {
            nmsRaid.removeFromRaid(VersionUtil.getCraftRaiderWrapper(raider).getHandle(), true);
            raider.remove();
        });

        this.loading = false;
    }

    private void setLastWave() {
        int secondLast = getDefaultWaveNumber(this.centre.getWorld());
        getNMSRaid().setGroupsSpawned(secondLast);
    }

    private AbstractRaidWrapper getNMSRaid() {
        AbstractBlockPositionWrapper blkPos = VersionUtil.getBlockPositionWrapper(centre.getX(), centre.getY(),
                centre.getZ());
        AbstractWorldServerWrapper level = VersionUtil.getCraftWorldWrapper(centre.getWorld()).getHandle();
        return level.getRaidAt(blkPos);
    }

    private void triggerRaid(Location location) {
        AbstractMinecraftServerWrapper nmsServer = VersionUtil.getCraftServerWrapper(Bukkit.getServer()).getServer();
        AbstractWorldServerWrapper nmsWorld = VersionUtil.getCraftWorldWrapper(location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "LargeRaids");
        AbstractPlayerEntityWrapper abstractPlayer = VersionUtil.getPlayerEntityWrapper(nmsServer,
                nmsWorld, profile);
        abstractPlayer.setPosition(location.getX(), location.getY(), location.getZ());

        AbstractRaidsWrapper raids = nmsWorld.getRaids();
        try {
            raids.createOrExtendRaid(abstractPlayer);
        } catch (NullPointerException e) {
            // Exception caused by failure to send packets to non-existing npc
        }
        raids.setDirty();

        AbstractRaidWrapper raid = this.getNMSRaid();
        raid.setBadOmenLevel(2);
        this.setRaid(VersionUtil.getCraftRaidWrapper(raid).getRaid());
    }

}
