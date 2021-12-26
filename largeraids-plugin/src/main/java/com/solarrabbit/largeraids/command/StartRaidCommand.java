package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.TriggerListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartRaidCommand extends TriggerListener implements CommandExecutor {

    public StartRaidCommand(LargeRaids plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            Location location = plugin.getDatabaseAdapter().getCentre(args[0]);
            if (location == null) {
                sender.sendMessage(ChatColor.RED + "There are no existing artificial village centers with that name!");
                return false;
            }
            triggerRaid(sender, location);
            return true;
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            triggerRaid(sender, player.getLocation());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void unregisterListener() {
        // Nothing to do here since this trigger isn't a configurable option.
    }

}
