package com.solarrabbit.largeraids.config.trigger;

import org.bukkit.configuration.ConfigurationSection;

public class TimeBombTriggerConfig implements TriggerConfig {
    private final boolean isEnabled;
    private final int tick;

    TimeBombTriggerConfig(ConfigurationSection config) {
        isEnabled = config.getBoolean("enabled");
        tick = config.getInt("tick");
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public int getTick() {
        return tick;
    }
}
