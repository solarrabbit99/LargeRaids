package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GiveSummonItemCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public GiveSummonItemCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1)
            return false;
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find specified player!");
            return false;
        }

        if (args.length < 2) {
            giveItems(sender, targetPlayer, 1);
            return true;
        } else {
            try {
                int amount = Integer.parseInt(args[1]);
                if (amount <= 0)
                    sender.sendMessage(ChatColor.RED + "Please input positive amount of items...");
                else
                    giveItems(sender, targetPlayer, amount);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid input amount of items...");
                return false;
            }
        }
    }

    private void giveItems(CommandSender requester, Player receiver, int requestAmount) {
        Inventory inventory = receiver.getInventory();
        for (int i = 0; i < requestAmount; i++) {
            inventory.addItem(plugin.getTriggerConfig().getDropInLavaConfig().getItem())
                    .forEach((index, item) -> receiver.getWorld().dropItem(receiver.getLocation(), item));
        }
        requester.sendMessage(ChatColor.GREEN + "Gave " + receiver.getName() + " " + requestAmount + " summoning "
                + (requestAmount == 1 ? "item" : "items"));
    }
}
