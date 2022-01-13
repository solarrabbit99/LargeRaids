package com.solarrabbit.largeraids.command.completer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.solarrabbit.largeraids.database.DatabaseAdapter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class StartRaidCommandCompleter implements TabCompleter {
    private final DatabaseAdapter db;

    public StartRaidCommandCompleter(DatabaseAdapter db) {
        this.db = db;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list.add("player");
                list.add("center");
                break;
            case 2:
                switch (args[0]) {
                    case "player":
                        list.addAll(
                                Bukkit.getOnlinePlayers().stream().map(Player::getName)
                                        .collect(Collectors.toSet()));
                        break;
                    case "center":
                        list.addAll(db.getCentres().keySet());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return list;
    }

}
