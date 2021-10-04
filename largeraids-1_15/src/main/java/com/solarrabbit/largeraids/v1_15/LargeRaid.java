package com.solarrabbit.largeraids.v1_15;

import java.util.List;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.raid.AbstractLargeRaid;
import com.solarrabbit.largeraids.util.ChatColorUtil;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.Raid.RaidStatus;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_15_R1.CraftRaid;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftRaider;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.WorldServer;
import net.minecraft.server.v1_15_R1.PersistentRaid;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.Raid;

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
        if (getNMSRaid() != null)
            return;

        RaidListener.addLargeRaid(this); // Register itself as large raid before RaidTriggerEvent
        triggerRaid(this.centre);
        this.loading = true; // IMPORTANT, else next wave will be triggered.

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (this.currentRaid.getStatus() == RaidStatus.ONGOING) {
                RaidListener.addLargeRaid(this);
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
        Raid raid = this.getNMSRaid();
        if (raid != null)
            raid.stop();
        RaidListener.removeLargeRaid(this);
    }

    @Override
    public boolean isSimilar(Location location) {
        BlockPosition blkPos = new BlockPosition(location.getX(), location.getY(), location.getZ());
        WorldServer level = ((CraftWorld) location.getWorld()).getHandle();
        return level.c_(blkPos) == getNMSRaid();
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
        getNMSRaid().heroes.clear();
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
        Raid nmsRaid = getNMSRaid();

        for (RaiderConfig raider : RaiderConfig.values()) {
            for (int i = 0; i < raider.getSpawnNumber(this.currentWave); i++) {
                net.minecraft.server.v1_15_R1.EntityRaider mob = raider.getNMSType()
                        .a(((CraftWorld) centre.getWorld()).getHandle());
                nmsRaid.a(getDefaultWaveNumber(this.centre.getWorld()), mob,
                        new BlockPosition(loc.getX(), loc.getY(), loc.getZ()), false);
            }
        }

        raiders.forEach(raider -> {
            nmsRaid.a(((CraftRaider) raider).getHandle(), true);
            raider.remove();
        });

        this.loading = false;
    }

    private void setLastWave() {
        int secondLast = getDefaultWaveNumber(this.centre.getWorld());
        try {
            FieldUtils.writeField(getNMSRaid(), "groupsSpawned", secondLast, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Raid getNMSRaid() {
        BlockPosition blkPos = new BlockPosition(centre.getX(), centre.getY(), centre.getZ());
        WorldServer level = ((CraftWorld) centre.getWorld()).getHandle();
        return level.c_(blkPos);
    }

    private void triggerRaid(Location location) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "LargeRaids");
        net.minecraft.server.v1_15_R1.EntityPlayer abstractPlayer = new net.minecraft.server.v1_15_R1.EntityPlayer(
                nmsServer, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        abstractPlayer.setPosition(location.getX(), location.getY(), location.getZ());

        PersistentRaid raids = nmsWorld.getPersistentRaid();
        try {
            raids.a(abstractPlayer);
        } catch (NullPointerException e) {
            // Exception caused by failure to send packets to non-existing npc
        }
        raids.b();

        Raid raid = this.getNMSRaid();
        raid.badOmenLevel = 2;
        this.setRaid(new CraftRaid(raid));
    }

}
