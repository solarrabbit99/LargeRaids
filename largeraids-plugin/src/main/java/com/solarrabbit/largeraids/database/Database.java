package com.solarrabbit.largeraids.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Encapsulates a database access and necessary methods for Duke's list.
 */
public abstract class Database {
    /** Name of database table. */
    protected static final String DATABASE_NAME = "plugin_data";
    protected static final String VILLAGES_TABLE_NAME = "custom_villages";
    protected Connection connection;
    protected final JavaPlugin plugin;

    protected Database(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns connection established by the database loader.
     *
     * @return SQL connection
     */
    public abstract Connection getSQLConnection();

    /**
     * Executes create table statement.
     */
    public abstract void load();

    /**
     * Initializes and tests SQL connection by attempting to execute select
     * statements from respective tables in the database.
     */
    protected void initialize() {
        connection = this.getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + VILLAGES_TABLE_NAME + ";");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public abstract Map<String, Location> getCentres();

    public abstract void addCentre(Location location, String name);

    public abstract void removeCentre(String index);

    public abstract Location getCentre(String name);

    /**
     * Releases both the {@link PreparedStatement} and {@link ResultSet} of the
     * database.
     *
     * @param ps PreparedStatement of the database
     * @param rs ResultSet of the database
     */
    protected void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (this.connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Closes {@link PreparedStatement} and {@link Connection} to the database.
     *
     * @param ps   PreparedStatement of the database
     * @param conn Connection to the database
     */
    protected void close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (this.connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected File createOrOpenDataFile() {
        File dataFolder = new File(this.plugin.getDataFolder(), DATABASE_NAME + ".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dataFolder;
    }

}
