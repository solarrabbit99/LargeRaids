package com.solarrabbit.largeraids.config.trigger;

import org.bukkit.configuration.ConfigurationSection;

public class OmenTriggerConfig implements TriggerConfig {
    private final boolean isEnabled;
    private final int maxLevel;

    OmenTriggerConfig(ConfigurationSection config) {
        isEnabled = config.getBoolean("enabled");
        maxLevel = config.getInt("max-level");
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
}
