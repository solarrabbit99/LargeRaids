package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartRaidCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            VersionUtil.createLargeRaid(player.getLocation()).startRaid();
        }
        return true;
    }

}
