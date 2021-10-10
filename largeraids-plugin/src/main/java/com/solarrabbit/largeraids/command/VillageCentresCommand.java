package com.solarrabbit.largeraids.command;

import java.util.Map;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.util.VersionUtil;
import org.bukkit.ChatColor;
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
        if (args.length == 0) {
            this.list(sender);
            return true;
        }

        if (!(sender instanceof Player))
            return true;

        switch (args[0]) {
            case "add":
                if (args.length < 2)
                    return false;
                this.add((Player) sender, args[1]);
                return true;
            case "remove":
                if (args.length < 2)
                    return false;
                this.remove((Player) sender, args[1]);
                return true;
            default:
                return false;
        }
    }

    private void add(Player player, String name) {
        Location center = this.plugin.getDatabaseAdapter().getCentre(name);

        if (center != null) {
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.add.already-exist"));
            return;
        }

        if (player.isFlying() || (VersionUtil.isAtLeast("v1_16_R3") && player.isInWater())) {
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.add.add-unsafe"));
            return;
        }

        Location newCenter = player.getLocation();
        Runnable ifSuccess = () -> {
            this.plugin.getDatabaseAdapter().addCentre(newCenter, name);
            player.sendMessage(ChatColor.GREEN + this.plugin.getMessage("village-centers.add.add-success"));
        };
        Runnable ifFail = () -> player
                .sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.add.add-fail"));
        VersionUtil.getVillageManager().addVillage(newCenter, ifSuccess, ifFail);
    }

    private void remove(Player player, String name) {
        Location center = this.plugin.getDatabaseAdapter().getCentre(name);

        if (center == null) {
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.remove.no-exist"));
            return;
        }
        VersionUtil.getVillageManager().removeVillage(center);
        this.plugin.getDatabaseAdapter().removeCentre(name);
        player.sendMessage(ChatColor.GREEN + this.plugin.getMessage("village-centers.remove.remove-success"));

    }

    private void list(CommandSender sender) {
        Map<String, Location> map = this.plugin.getDatabaseAdapter().getCentres();
        if (map.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + this.plugin.getMessage("village-centers.list.no-exist"));
            return;
        }
        map.forEach((str, loc) -> {
            sender.sendMessage(ChatColor.GREEN + str + " " + getLocString(loc));
        });
    }

    private String getLocString(Location loc) {
        return "[" + loc.getWorld().getName() + ", " + getRoundedDouble(loc.getX()) + ", "
                + getRoundedDouble(loc.getY()) + ", " + getRoundedDouble(loc.getZ()) + "]";
    }

    private String getRoundedDouble(double d) {
        return String.format("%.3f", d);
    }

}
