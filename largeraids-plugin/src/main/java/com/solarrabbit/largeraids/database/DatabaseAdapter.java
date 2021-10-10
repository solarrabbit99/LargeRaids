package com.solarrabbit.largeraids.database;

import java.util.Map;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.PluginLogger.Level;
import org.bukkit.Location;

public class DatabaseAdapter {
    private final LargeRaids plugin;
    private final Database db;
    private Map<String, Location> map;

    public DatabaseAdapter(LargeRaids plugin) {
        this.plugin = plugin;
        this.db = new SQLite(plugin);
    }

    public Map<String, Location> getCentres() {
        return this.map;
    }

    public void addCentre(Location location, String name) {
        this.map.put(name, location);
        this.db.addCentre(location, name).exceptionally(ex -> {
            this.plugin.log("village-centers.database.add-fail", Level.WARN);
            ex.printStackTrace();
            return null;
        });
    }

    public void removeCentre(String name) {
        this.map.remove(name);
        this.db.removeCentre(name).exceptionally(ex -> {
            this.plugin.log(this.plugin.getMessage("village-centers.database.remove-fail"), Level.WARN);
            ex.printStackTrace();
            return null;
        });
    }

    public Location getCentre(String name) {
        return this.map.get(name);
    }

    public void load() {
        this.db.load();
        this.plugin.log("Loading artificial villages centers...", Level.INFO);
        this.db.getCentres().thenAccept(m -> this.map = m)
                .thenRun(() -> this.plugin.log("Loaded artificial villages centers.", Level.SUCCESS))
                .exceptionally(e -> {
                    this.plugin.log("Unable to access database!", Level.FAIL);
                    return null;
                });
    }
}
