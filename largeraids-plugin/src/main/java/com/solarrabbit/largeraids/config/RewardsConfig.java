package com.solarrabbit.largeraids.config;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.util.ChatColorUtil;
import com.solarrabbit.largeraids.util.ItemCreator;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RewardsConfig {
    private final String message;
    private final ItemStack[] items;
    private final String[] commands;
    private final int minRaiderKills;
    private final double minDamageDeal;
    private final int heroLevel;
    private final int heroDuration;

    public RewardsConfig(ConfigurationSection config) {
        message = config.getString("message", null);
        ConfigurationSection itemsConfig = config.getConfigurationSection("items");
        items = itemsConfig.getKeys(false).stream()
                .map(key -> ItemCreator.getItemFromConfig(itemsConfig.getConfigurationSection(key)))
                .toArray(ItemStack[]::new);
        commands = config.getStringList("commands").toArray(new String[0]);
        minRaiderKills = config.getInt("min-raider-kills");
        minDamageDeal = config.getDouble("min-damage-deal");
        ConfigurationSection heroConfig = config.getConfigurationSection("hero-of-the-village");
        heroLevel = heroConfig.getInt("level");
        heroDuration = heroConfig.getInt("duration");
    }

    @Nullable
    public String getMessage() {
        return message == null ? null : ChatColorUtil.colorize(message);
    }

    public ItemStack[] getItems() {
        return items.clone();
    }

    public String[] getCommands() {
        return commands.clone();
    }

    public int getMinRaiderKills() {
        return minRaiderKills;
    }

    public double getMinDamageDeal() {
        return minDamageDeal;
    }

    public int getHeroLevel() {
        return heroLevel;
    }

    public int getHeroDuration() {
        return heroDuration;
    }
}
