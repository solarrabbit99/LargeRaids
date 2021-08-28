package com.solarrabbit.largeraids.command;

import java.util.Map;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VillageCentresCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public VillageCentresCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        if (args.length == 0)
            return false;
        switch (args[0]) {
        case "add":
            if (args.length < 2)
                return false;
            return this.add((Player) sender, args[1]);
        case "remove":
            if (args.length < 2)
                return false;
            return this.remove(args[1]);
        case "list":
            return this.list((Player) sender);
        default:
            return false;
        }
    }

    private boolean add(Player player, String name) {
        this.plugin.getDatabase().addCentre(player.getLocation(), name);
        return true;
    }

    private boolean remove(String name) {
        this.plugin.getDatabase().removeCentre(name);
        return true;
    }

    private boolean list(Player player) {
        Map<String, Location> map = this.plugin.getDatabase().getCentres();
        map.forEach((str, loc) -> {
            player.sendMessage(str + ": " + loc);
        });
        return true;
    }

}
