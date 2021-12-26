package com.solarrabbit.largeraids.config;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.item.ItemCreator;
import com.solarrabbit.largeraids.util.ChatColorUtil;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class RewardsConfig {
    private final String message;
    private final ItemStack[] items;
    private final String[] commands;

    RewardsConfig(ConfigurationSection config) {
        message = config.getString("message", null);
        ConfigurationSection itemsConfig = config.getConfigurationSection("items");
        items = itemsConfig.getKeys(false).stream()
                .map(key -> ItemCreator.getItemFromConfig(itemsConfig.getConfigurationSection(key)))
                .toArray(ItemStack[]::new);
        commands = config.getStringList("commands").toArray(String[]::new);
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
}
