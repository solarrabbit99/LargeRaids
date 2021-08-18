package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartRaidCommand implements CommandExecutor {
    private LargeRaids plugin;

    public StartRaidCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            new LargeRaid(this.plugin, player.getLocation()).startRaid();
        }
        return true;
    }
}
