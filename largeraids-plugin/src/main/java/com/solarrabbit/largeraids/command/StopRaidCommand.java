package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.RaidManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopRaidCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public StopRaidCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        RaidManager listener = plugin.getRaidManager();
        if (args.length >= 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                listener.getLargeRaid(player.getLocation()).ifPresent(raid -> {
                    raid.stopRaid();
                    listener.currentRaids.remove(raid);
                });
                return true;
            }
            return false;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            listener.getLargeRaid(player.getLocation()).ifPresent(raid -> {
                raid.stopRaid();
                listener.currentRaids.remove(raid);
            });
            return true;
        } else {
            return true;
        }
    }

}
