package com.solarrabbit.largeraids.v1_17;

import java.util.List;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.ChatColorUtil;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.craftbukkit.v1_17_R1.CraftRaid;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftRaider;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.saveddata.SavedData;

public class LargeRaid extends AbstractLargeRaid {

    public LargeRaid(LargeRaids plugin, Location location, Player player) {
        super(plugin, location, player);
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
        if (getNMSRaid() != null)
            return;

        triggerRaid(this.centre);
        this.loading = true;

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

    @Override
    public void stopRaid() {
        this.getNMSRaid().stop();
        RaidListener.removeLargeRaid(this);
    }

    @Override
    public boolean isSimilar(Location location) {
        BlockPos blkPos = new BlockPos(location.getX(), location.getY(), location.getZ());
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        return level.getRaidAt(blkPos) == getNMSRaid();
    }

    @Override
    public void triggerNextWave() {
        if (this.isLastWave())
            return;

        this.loading = true;

        if (!needTrigger()) {
            this.currentWave++;
            this.broadcastWave();
            return;
        }

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        getNMSRaid().stop();

        this.currentWave++;
        this.broadcastWave();
        triggerRaid(this.centre);
    }

    @Override
    public void spawnNextWave() {
        List<Raider> raiders = this.currentRaid.getRaiders();

        if (!this.loading) {
            if (this.needTrigger()) {
                for (Raider raider : raiders)
                    raider.remove();
                triggerNextWave();
                return;
            } else {
                triggerNextWave();
            }
        }

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

}
