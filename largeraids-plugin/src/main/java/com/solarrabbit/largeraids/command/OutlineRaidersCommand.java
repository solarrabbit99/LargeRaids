package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.raid.RaidManager;
import com.solarrabbit.largeraids.raid.RaidersOutliner;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Outlines all raiders in a raid, regardless whether it is a large raid or not.
 */
public class OutlineRaidersCommand extends RaidersOutliner implements CommandExecutor {
    private final RaidManager manager;

    public OutlineRaidersCommand(RaidManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;
        Location location = ((Player) sender).getLocation();
        manager.getRaid(location).filter(raid -> !raid.getRaiders().isEmpty()).ifPresent(raid -> {
            resonateBell(location);
            outlineAllRaiders(raid);
        });
        return true;
    }

}
