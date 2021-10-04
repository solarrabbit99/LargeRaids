package com.solarrabbit.largeraids;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import com.solarrabbit.largeraids.PluginLogger.Level;
import com.solarrabbit.largeraids.command.GiveSummonItemCommand;
import com.solarrabbit.largeraids.command.ReloadPlugin;
import com.solarrabbit.largeraids.command.StartRaidCommand;
import com.solarrabbit.largeraids.command.StopRaidCommand;
import com.solarrabbit.largeraids.command.VillageCentresCommand;
import com.solarrabbit.largeraids.config.RaiderConfig;
import com.solarrabbit.largeraids.database.Database;
import com.solarrabbit.largeraids.database.SQLite;
import com.solarrabbit.largeraids.listener.DropInLavaTriggerListener;
import com.solarrabbit.largeraids.listener.NewMoonTriggerListener;
import com.solarrabbit.largeraids.listener.RaidListener;
import com.solarrabbit.largeraids.listener.TriggerListener;
import com.solarrabbit.largeraids.listener.omen.VillageAbsorbOmenListener;
import com.solarrabbit.largeraids.support.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LargeRaids extends JavaPlugin {
    private YamlConfiguration messages;
    private PluginLogger logger;
    private Database db;
    private Set<TriggerListener> registeredTriggerListeners;
    private RaiderConfig raiderConfig;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.logger = new PluginLogger();

        this.db = new SQLite(this);
        this.db.load();

        RaidListener mainListener = new RaidListener(this);
        this.getServer().getPluginManager().registerEvents(mainListener, this);
        mainListener.init();

        this.getCommand("lrstart").setExecutor(new StartRaidCommand());
        this.getCommand("lrstop").setExecutor(new StopRaidCommand());
        this.getCommand("lrgive").setExecutor(new GiveSummonItemCommand());
        this.getCommand("lrreload").setExecutor(new ReloadPlugin(this));
        this.getCommand("lrcenters").setExecutor(new VillageCentresCommand(this));

        this.loadMessages();
        this.testConfig();
        this.raiderConfig = new RaiderConfig(this);
        this.registerTriggers();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholder().register();
        }
    }

    public void log(String message, Level level) {
        this.logger.sendMessage(message, level);
        if (level == Level.FAIL) {
            this.logger.sendMessage("Disabling plugin...", level);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(this, key);
    }

    public void reload() {
        this.reloadConfig();
        this.testConfig();
        this.raiderConfig = new RaiderConfig(this);
        this.registerTriggers();
    }

    public String getMessage(String node) {
        return this.messages.getString(node, "");
    }

    public Database getDatabase() {
        return this.db;
    }

    public RaiderConfig getRaiderConfig() {
        return this.raiderConfig;
    }

    private void loadMessages() {
        messages = new YamlConfiguration();
        try {
            messages.load(new InputStreamReader(this.getResource("messages.yml")));
        } catch (IOException | InvalidConfigurationException e) {
            this.log("Unable to load messages!", Level.FAIL);
        }
    }

    private void testConfig() {
        int totalWaves = this.getConfig().getInt("raid.waves");
        ConfigurationSection section = this.getConfig().getConfigurationSection("raid.mobs");
        for (String mob : section.getKeys(false)) {
            if (section.getIntegerList(mob).size() < totalWaves) {
                this.log(this.messages.getString("config.invalid-mob-array-length"), Level.FAIL);
                return;
            }
        }
        for (int i = 0; i < totalWaves; i++) {
            final int wave = i;
            int totalRaiders = section.getKeys(false).stream().map(key -> section.getIntegerList(key).get(wave))
                    .reduce(0, (x, y) -> x + y);
            if (totalRaiders == 0) {
                this.log(this.messages.getString("config.zero-raider-wave"), Level.FAIL);
                return;
            }
        }
    }

    private void registerTriggers() {
        unregisterTriggers();

        if (testTrigger("omen"))
            registerTrigger(new VillageAbsorbOmenListener(this), true);
        if (testTrigger("drop-item-in-lava"))
            registerTrigger(new DropInLavaTriggerListener(), true);
        if (testTrigger("new-moon"))
            registerTrigger(new NewMoonTriggerListener(this), false);
    }

    private void unregisterTriggers() {
        if (this.registeredTriggerListeners != null) {
            for (TriggerListener listener : registeredTriggerListeners) {
                listener.unregisterListener();
            }
        }
        this.registeredTriggerListeners = new HashSet<>();
    }

    private void registerTrigger(TriggerListener listener, boolean registerEvents) {
        if (registerEvents)
            this.getServer().getPluginManager().registerEvents(listener, this);
        this.registeredTriggerListeners.add(listener);
    }

    private boolean testTrigger(String trigger) {
        return getConfig().getBoolean("trigger." + trigger + ".enabled");
    }

}
