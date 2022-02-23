package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadPluginCommand implements CommandExecutor {
    private LargeRaids plugin;

    public ReloadPluginCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.plugin.reload();
        sender.sendMessage(
                ChatColor.GOLD + "[LargeRaids] " + ChatColor.GREEN + "Successfully reloaded configurations!");
        return true;
    }
}
