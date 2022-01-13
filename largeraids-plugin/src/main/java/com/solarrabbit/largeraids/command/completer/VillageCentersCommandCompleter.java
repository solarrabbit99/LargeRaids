package com.solarrabbit.largeraids.command.completer;

import java.util.ArrayList;
import java.util.List;

import com.solarrabbit.largeraids.database.DatabaseAdapter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class VillageCentersCommandCompleter implements TabCompleter {
    private final DatabaseAdapter db;

    public VillageCentersCommandCompleter(DatabaseAdapter db) {
        this.db = db;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 1:
                list.add("add");
                list.add("remove");
                list.add("show");
                list.add("hide");
                break;
            case 2:
                switch (args[0]) {
                    case "remove":
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
