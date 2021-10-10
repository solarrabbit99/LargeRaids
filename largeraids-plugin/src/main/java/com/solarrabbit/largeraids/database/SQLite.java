package com.solarrabbit.largeraids.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * SQLite implementation of accessing the task database.
 */
public class SQLite extends Database {
    private static final String SQLITE_CREATE_VILLAGES_TABLE_STATEMENT = "CREATE TABLE IF NOT EXISTS "
            + VILLAGES_TABLE_NAME
            + " (`name` STRING NOT NULL, `world` STRING NOT NULL, `x` REAL NOT NULL, `y` REAL NOT NULL, `z` REAL NOT NULL, "
            + "PRIMARY KEY (`name`));";

    public SQLite(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public CompletableFuture<Connection> getSQLConnection() {
        return CompletableFuture.supplyAsync(() -> this.createOrOpenDataFile()).thenApply(dataFile -> {
            try {
                Class.forName("org.sqlite.JDBC");
                return DriverManager.getConnection("jdbc:sqlite:" + dataFile);
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public void load() {
        getSQLConnection().thenAccept(connection -> {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate(SQLITE_CREATE_VILLAGES_TABLE_STATEMENT);
                s.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            testConnection(connection);
        });
    }

    @Override
    public CompletableFuture<Map<String, Location>> getCentres() {
        Map<String, Location> centres = new HashMap<>();
        return getSQLConnection().thenApply(connection -> {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + VILLAGES_TABLE_NAME + ";");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    World world = this.plugin.getServer().getWorld(rs.getString("world"));
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    Location location = new Location(world, x, y, z);
                    centres.put(name, location);
                }
                close(ps, connection);
                return centres;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> addCentre(Location location, String name) {
        return getSQLConnection().thenAccept(connection -> {
            try {
                PreparedStatement ps = connection.prepareStatement("REPLACE INTO " + VILLAGES_TABLE_NAME
                        + " (name, world, x, y, z) VALUES('" + name + "', '" + location.getWorld().getName() + "', "
                        + location.getX() + ", " + location.getY() + ", " + location.getZ() + ");");
                ps.executeUpdate();
                close(ps, connection);
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Void> removeCentre(String name) {
        return getSQLConnection().thenAccept(connection -> {
            try {
                PreparedStatement ps = connection
                        .prepareStatement("DELETE FROM " + VILLAGES_TABLE_NAME + " WHERE name = '" + name + "';");
                ps.executeUpdate();
                close(ps, connection);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public CompletableFuture<Location> getCentre(String name) {
        return getSQLConnection().thenApply(connection -> {
            try {
                Location location = null;
                PreparedStatement ps = connection
                        .prepareStatement("SELECT * FROM " + VILLAGES_TABLE_NAME + " WHERE name = '" + name + "';");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    World world = this.plugin.getServer().getWorld(rs.getString("world"));
                    double x = rs.getDouble("x");
                    double y = rs.getDouble("y");
                    double z = rs.getDouble("z");
                    location = new Location(world, x, y, z);
                }
                close(ps, connection);
                return location;
            } catch (SQLException ex) {
                return null;
            }
        });
    }

}
