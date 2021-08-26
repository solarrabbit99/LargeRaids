/*
 *  This file is part of GoatHorn. Copyright (c) 2021 SolarRabbit.
 *
 *  GoatHorn is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GoatHorn is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with GoatHorn. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
import com.solarrabbit.largeraids.listener.DropInLavaTriggerListener;
import com.solarrabbit.largeraids.listener.NewMoonTriggerListener;
import com.solarrabbit.largeraids.listener.RaidListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LargeRaids extends JavaPlugin {
    private YamlConfiguration messages;
    private PluginLogger logger;
    private Set<Integer> configurableTasks;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.logger = new PluginLogger();

        RaidListener mainListener = new RaidListener(this);
        this.getServer().getPluginManager().registerEvents(mainListener, this);
        mainListener.init();

        this.getCommand("lrstart").setExecutor(new StartRaidCommand());
        this.getCommand("lrstop").setExecutor(new StopRaidCommand());
        this.getCommand("lrgive").setExecutor(new GiveSummonItemCommand());
        this.getCommand("lrreload").setExecutor(new ReloadPlugin(this));

        this.loadMessages();
        this.testConfig();
        this.registerTriggers();
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
        this.registerTriggers();
    }

    public String getMessage(String node) {
        return this.messages.getString(node, "");
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
        for (World world : getServer().getWorlds()) {
            if (totalWaves < AbstractLargeRaid.getDefaultWaveNumber(world) + 1) {
                this.log(this.messages.getString("config.invalid-wave-number"), Level.FAIL);
                return;
            }
        }
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
        PlayerDropItemEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
        if (this.configurableTasks != null) {
            for (Integer id : configurableTasks) {
                Bukkit.getScheduler().cancelTask(id);
            }
        }
        this.configurableTasks = new HashSet<>();

        PluginManager manager = this.getServer().getPluginManager();
        if (testTrigger("drop-item-in-lava"))
            manager.registerEvents(new DropInLavaTriggerListener(), this);
        if (testTrigger("new-moon"))
            this.configurableTasks.add(new NewMoonTriggerListener(this).init());
    }

    private boolean testTrigger(String trigger) {
        return getConfig().getBoolean("trigger." + trigger + ".enabled");
    }

}
