package com.solarrabbit.largeraids.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
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
    public Connection getSQLConnection() {
        File dataFile = this.createOrOpenDataFile();
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFile);
            return connection;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void load() {
        this.connection = getSQLConnection();
        try {
            Statement s = this.connection.createStatement();
            s.executeUpdate(SQLITE_CREATE_VILLAGES_TABLE_STATEMENT);
            s.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        initialize();
    }

    @Override
    public Map<String, Location> getCentres() {
        Map<String, Location> centres = new HashMap<>();
        try {
            this.connection = getSQLConnection();
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
            close(ps);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return centres;
    }

    @Override
    public void addCentre(Location location, String name) {
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection.prepareStatement("REPLACE INTO " + VILLAGES_TABLE_NAME
                    + " (name, world, x, y, z) VALUES('" + name + "', '" + location.getWorld().getName() + "', "
                    + location.getX() + ", " + location.getY() + ", " + location.getZ() + ");");
            ps.executeUpdate();
            close(ps);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeCentre(String name) {
        try {
            connection = getSQLConnection();
            PreparedStatement ps = connection
                    .prepareStatement("DELETE FROM " + VILLAGES_TABLE_NAME + " WHERE name = '" + name + "';");
            ps.executeUpdate();
            close(ps);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
