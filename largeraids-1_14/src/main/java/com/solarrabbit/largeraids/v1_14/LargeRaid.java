package com.solarrabbit.largeraids.v1_14;

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
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_14_R1.CraftRaid;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftRaider;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.MinecraftServer;
import net.minecraft.server.v1_14_R1.WorldServer;
import net.minecraft.server.v1_14_R1.PersistentRaid;
import net.minecraft.server.v1_14_R1.PlayerInteractManager;

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
        this.getNMSRaid().n();
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
        if (this.isLastWave())
            return;

        this.loading = true;

        if (!needTrigger()) {
            this.currentWave++;
            this.broadcastWave();
            this.setLastWave();
            return;
        }

        currentRaid.getHeroes().forEach(uuid -> pendingHeroes.add(uuid));
        getNMSRaid().n();

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
        net.minecraft.server.v1_14_R1.Raid nmsRaid = getNMSRaid();

        for (RaiderConfig raider : RaiderConfig.values()) {
            for (int i = 0; i < raider.getSpawnNumber(this.currentWave); i++) {
                net.minecraft.server.v1_14_R1.EntityRaider mob = raider.getNMSType()
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
            FieldUtils.writeField(getNMSRaid(), "q", secondLast, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private net.minecraft.server.v1_14_R1.Raid getNMSRaid() {
        BlockPosition blkPos = new BlockPosition(centre.getX(), centre.getY(), centre.getZ());
        WorldServer level = ((CraftWorld) centre.getWorld()).getHandle();
        return level.c_(blkPos);
    }

    private void triggerRaid(Location location) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "LargeRaids");
        net.minecraft.server.v1_14_R1.EntityPlayer abstractPlayer = new net.minecraft.server.v1_14_R1.EntityPlayer(
                nmsServer, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        abstractPlayer.setPosition(location.getX(), location.getY(), location.getZ());

        PersistentRaid raids = nmsWorld.C();
        try {
            raids.a(abstractPlayer);
        } catch (NullPointerException e) {
            // Exception caused by failure to send packets to non-existing npc
        }
        raids.b();

        net.minecraft.server.v1_14_R1.Raid raid = this.getNMSRaid();
        raid.o = 5;
        this.setRaid(new CraftRaid(raid));
    }

}
