package com.solarrabbit.largeraids.command;

import java.util.Map;

import com.solarrabbit.largeraids.LargeRaids;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class VillageCentresCommand implements CommandExecutor {
    private final LargeRaids plugin;
    private boolean isShowing;
    private int taskId;

    public VillageCentresCommand(LargeRaids plugin) {
        this.plugin = plugin;
        this.isShowing = false;
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
            case "show":
                show();
                return true;
            case "hide":
                hide();
                return true;
            default:
                return false;
        }
    }

    private void show() {
        if (isShowing)
            return;
        Map<String, Location> centers = plugin.getDatabaseAdapter().getCentres();
        taskId = Bukkit.getScheduler()
                .runTaskTimer(plugin, () -> centers.values().forEach(loc -> {
                    highlightLocation(loc);
                    plotSphere(loc, 96);
                }), 0, 20)
                .getTaskId();
        isShowing = true;
    }

    private void hide() {
        if (isShowing)
            Bukkit.getScheduler().cancelTask(taskId);
        isShowing = false;
    }

    private void add(Player player, String name) {
        Location center = this.plugin.getDatabaseAdapter().getCentre(name);

        if (center != null) {
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.add.already-exist"));
            return;
        }

        Location newCenter = player.getLocation();
        if (plugin.getVillageManager().addVillage(newCenter)) {
            plugin.getDatabaseAdapter().addCentre(newCenter, name);
            player.sendMessage(ChatColor.GREEN + this.plugin.getMessage("village-centers.add.add-success"));
        } else
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.add.add-fail"));
    }

    private void remove(Player player, String name) {
        Location center = plugin.getDatabaseAdapter().getCentre(name);

        if (center == null) {
            player.sendMessage(ChatColor.RED + this.plugin.getMessage("village-centers.remove.no-exist"));
            return;
        }

        if (center.getWorld() != null)
            plugin.getVillageManager().removeVillage(center);
        plugin.getDatabaseAdapter().removeCentre(name);
        player.sendMessage(ChatColor.GREEN + this.plugin.getMessage("village-centers.remove.remove-success"));

    }

    private void list(CommandSender sender) {
        Map<String, Location> map = this.plugin.getDatabaseAdapter().getCentres();
        if (map.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + this.plugin.getMessage("village-centers.list.no-exist"));
            return;
        }
        map.forEach((str, loc) -> sender.spigot().sendMessage(getClickableComponent(str, loc)));
    }

    private TextComponent getClickableComponent(String name, Location loc) {
        TextComponent text = new TextComponent(name + " " + getLocString(loc));
        text.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport")));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getTeleportCommand(loc)));
        return text;
    }

    private String getTeleportCommand(Location loc) {
        return String.join(" ", "/tp", "@s", String.valueOf(loc.getX()), String.valueOf(loc.getY()),
                String.valueOf(loc.getZ()));
    }

    private String getLocString(Location loc) {
        return "[" + (loc.getWorld() == null ? "null" : loc.getWorld().getName()) + ", " + getRoundedDouble(loc.getX())
                + ", " + getRoundedDouble(loc.getY()) + ", " + getRoundedDouble(loc.getZ()) + "]";
    }

    private String getRoundedDouble(double d) {
        return String.format("%.3f", d);
    }

    private void highlightLocation(Location location) {
        if (location.getWorld() == null || !location.getChunk().isLoaded())
            return;
        location.getWorld().spawnParticle(Particle.CRIT_MAGIC, location, 50, 0.5, 0.5, 0.5, 0);
    }

    private void plotSphere(Location center, double radius) {
        World world = center.getWorld();
        if (world == null || !center.getChunk().isLoaded())
            return;

        DustOptions options = new DustOptions(Color.ORANGE, 200.0F);
        Location mutLoc = center.clone();
        final int freq = 25;

        for (int i = 0; i <= freq; i++) {
            double y = (double) (2 * i - freq) / freq;
            double r = Math.cos(Math.asin(y));
            for (int j = 0; j < freq; j++) {
                double alpha = 2 * Math.PI * j / freq;
                double x = Math.sin(alpha);
                double z = Math.cos(alpha);

                mutLoc.setX((x * radius * r) + center.getBlockX());
                mutLoc.setZ((z * radius * r) + center.getBlockZ());
                mutLoc.setY((y * radius) + center.getBlockY());
                world.spawnParticle(Particle.REDSTONE, mutLoc, 1, 1, 1, 1, 0, options, true);
            }
        }
    }
}
