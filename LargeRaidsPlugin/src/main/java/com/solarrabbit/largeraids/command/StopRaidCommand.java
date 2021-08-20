package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRaidCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            RaidListener.matchingLargeRaid(player.getLocation()).ifPresent(raid -> raid.stopRaid());
        }
        return true;
    }

}
