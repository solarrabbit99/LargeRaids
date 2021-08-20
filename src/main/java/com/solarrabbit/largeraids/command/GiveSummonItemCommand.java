package com.solarrabbit.largeraids.command;

import com.solarrabbit.largeraids.item.SummonItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GiveSummonItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find specified player!");
            return false;
        }

        if (args.length < 2) {
            this.giveItems(sender, targetPlayer, 1);
            return true;
        } else {
            int amount = getPositiveInteger(args[1]);
            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "Invalid input amount of items...");
                return false;
            } else {
                giveItems(sender, targetPlayer, amount);
                return true;
            }
        }
    }

    private void giveItems(CommandSender requester, Player receiver, int requestAmount) {
        Inventory inventory = receiver.getInventory();
        for (int i = 0; i < requestAmount; i++) {
            inventory.addItem(new SummonItem())
                    .forEach((index, item) -> receiver.getWorld().dropItem(receiver.getLocation(), item));
        }
        requester.sendMessage(ChatColor.GREEN + "Gave " + receiver.getName() + " " + requestAmount + " summoning item");
    }

    private int getPositiveInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
