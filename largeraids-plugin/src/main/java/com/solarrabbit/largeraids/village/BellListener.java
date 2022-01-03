package com.solarrabbit.largeraids.village;

import java.util.List;
import java.util.Optional;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BellListener implements Listener {
    /** Default duration for glowing effect (in ticks). */
    private static final int DEFAULT_GLOW_TICK = 60;
    /** Offset from ringing of bell to application of effect (in ticks). */
    private static final int OFFSET_TICK = 45;
    /** Maximum distance from bell in which raiders would have been affected. */
    private static final int INNER_RADIUS = 32;
    private final LargeRaids plugin;
    private final RaidManager manager;
    private Location lastBellLoc;

    public BellListener(LargeRaids plugin) {
        this.plugin = plugin;
        manager = plugin.getRaidManager();
    }

    @EventHandler
    public void onPlayerRingBell(PlayerInteractEvent evt) {
        if (evt.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)
            return;
        Block block = evt.getClickedBlock();
        if (block.getType() != Material.BELL)
            return;
        lastBellLoc = block.getLocation();
    }

    @EventHandler
    public void onPlayerRingBell(PlayerStatisticIncrementEvent evt) {
        if (evt.getStatistic() != Statistic.BELL_RING)
            return;
        Raid raid = manager.getRaid(lastBellLoc).orElse(null);
        if (raid == null)
            return;
        Optional<LargeRaid> largeRaid = manager.getLargeRaid(raid);

        if ((largeRaid.isPresent() && plugin.getMiscConfig().shouldBellOutlineLarge())
                || (!largeRaid.isPresent() && plugin.getMiscConfig().shouldBellOutlineNormal()))
            outlineAllRaiders(raid, lastBellLoc);
    }

    private void outlineAllRaiders(Raid raid, Location source) {
        List<Raider> raiders = raid.getRaiders();
        if (raiders.isEmpty())
            return;
        boolean anyClose = raiders.stream()
                .anyMatch(raider -> raider.getLocation().distanceSquared(source) < Math.pow(INNER_RADIUS, 2));
        if (!anyClose)
            source.getWorld().playSound(source, Sound.BLOCK_BELL_RESONATE, 1, 1);
        int confDuration = plugin.getMiscConfig().getBellOutlineDuration() * 20;
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING,
                confDuration > 0 ? confDuration : DEFAULT_GLOW_TICK, 0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> raiders.forEach(raider -> raider.addPotionEffect(effect)),
                OFFSET_TICK);
    }
}
