package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.raid.RaidManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipWaveCommand implements CommandExecutor {
    private final RaidManager manager;
    private static final String LAST_WAVE_MESSAGE = ChatColor.GOLD + "The raid is now in its last wave. "
            + "If you want to stop the raid, use /lrstop instead!";

    public SkipWaveCommand(RaidManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Location loc = ((Player) sender).getLocation();
        manager.getLargeRaid(loc).ifPresent(raid -> {
            if (raid.isLastWave()) {
                sender.sendMessage(LAST_WAVE_MESSAGE);
                return;
            }
            manager.setIdle();
            raid.skipWave();
            manager.setActive();
        });
        return true;
    }

}
