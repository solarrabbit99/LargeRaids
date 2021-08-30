package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRaidCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                RaidListener.matchingLargeRaid(player.getLocation()).ifPresent(raid -> raid.stopRaid());
                return true;
            }
            return false;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            RaidListener.matchingLargeRaid(player.getLocation()).ifPresent(raid -> raid.stopRaid());
            return true;
        } else {
            return true;
        }
    }

}
