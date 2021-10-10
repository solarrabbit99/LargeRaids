package com.solarrabbit.largeraids.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Encapsulates a database access and necessary methods for Duke's list.
 */
public abstract class Database {
    /** Name of database table. */
    protected static final String DATABASE_NAME = "plugin_data";
    protected static final String VILLAGES_TABLE_NAME = "custom_villages";
    protected final JavaPlugin plugin;

    protected Database(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns connection established by the database loader.
     *
     * @return SQL connection
     */
    public abstract CompletableFuture<Connection> getSQLConnection();

    /**
     * Executes create table statement.
     */
    public abstract void load();

    /**
     * Tests SQL connection by attempting to execute select statements from
     * respective tables in the database. The provided connection will be closed
     * after.
     * 
     * @param connection used by {@link #load()}
     */
    protected void testConnection(Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + VILLAGES_TABLE_NAME + ";");
            ps.executeQuery();
            close(ps, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract CompletableFuture<Map<String, Location>> getCentres();

    public abstract CompletableFuture<Void> addCentre(Location location, String name);

    public abstract CompletableFuture<Void> removeCentre(String name);

    /**
     * @deprecated {@link DatabaseAdapter} should already have the mapping.
     */
    @Deprecated
    public abstract CompletableFuture<Location> getCentre(String name);

    /**
     * Releases both the {@link Statement} and {@link Connection} of the database.
     *
     * @param statement  opened by the database
     * @param connection with the database
     */
    protected void close(Statement statement, Connection connection) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected File createOrOpenDataFile() {
        File dataFile = new File(this.plugin.getDataFolder(), DATABASE_NAME + ".db");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return dataFile;
    }

}
