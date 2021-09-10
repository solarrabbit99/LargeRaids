package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.listener.TriggerListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartRaidCommand extends TriggerListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                triggerRaid(player.getLocation(), player);
                return true;
            }
            return false;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            triggerRaid(player.getLocation(), player);
            return true;
        } else {
            return false;
        }
    }

}
