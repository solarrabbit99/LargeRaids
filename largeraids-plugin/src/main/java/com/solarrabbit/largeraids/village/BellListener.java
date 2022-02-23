package com.solarrabbit.largeraids.village;

import java.util.Optional;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.raid.RaidManager;
import com.solarrabbit.largeraids.raid.RaidersOutliner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public class BellListener extends RaidersOutliner implements Listener {
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
        if (raid.getRaiders().isEmpty())
            return;
        if (!anyRaidersInRange(raid, source))
            resonateBell(source);
        int duration = plugin.getMiscConfig().getBellOutlineDuration() * 20;
        if (duration > 0)
            outlineAllRaiders(raid, duration);
        else
            outlineAllRaiders(raid);
    }
}
